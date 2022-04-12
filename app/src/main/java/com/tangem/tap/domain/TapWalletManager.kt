package com.tangem.tap.domain

import com.tangem.blockchain.blockchains.solana.RentProvider
import com.tangem.blockchain.common.*
import com.tangem.common.services.Result
import com.tangem.tap.common.ThrottlerWithValues
import com.tangem.tap.common.extensions.dispatchOnMain
import com.tangem.tap.common.extensions.safeUpdate
import com.tangem.tap.common.extensions.stripZeroPlainString
import com.tangem.tap.common.extensions.withMainContext
import com.tangem.tap.common.redux.global.FiatCurrencyName
import com.tangem.tap.common.redux.global.GlobalAction
import com.tangem.tap.currenciesRepository
import com.tangem.tap.domain.TapWorkarounds.derivationStyle
import com.tangem.tap.domain.TapWorkarounds.isStart2Coin
import com.tangem.tap.domain.TapWorkarounds.isTestCard
import com.tangem.tap.domain.configurable.config.ConfigManager
import com.tangem.tap.domain.extensions.makePrimaryWalletManager
import com.tangem.tap.domain.extensions.makeWalletManagersForApp
import com.tangem.tap.domain.tasks.product.ScanResponse
import com.tangem.tap.domain.tokens.BlockchainNetwork
import com.tangem.tap.features.demo.isDemoCard
import com.tangem.tap.features.wallet.models.PendingTransactionType
import com.tangem.tap.features.wallet.models.getPendingTransactions
import com.tangem.tap.features.wallet.redux.Currency
import com.tangem.tap.features.wallet.redux.WalletAction
import com.tangem.tap.network.NetworkConnectivity
import com.tangem.tap.network.coinmarketcap.CoinMarketCapService
import com.tangem.tap.store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal


class TapWalletManager {
    val walletManagerFactory: WalletManagerFactory
            by lazy { WalletManagerFactory(blockchainSdkConfig) }

    private val coinMarketCapService = CoinMarketCapService()
    private val blockchainSdkConfig by lazy {
        store.state.globalState.configManager?.config?.blockchainSdkConfig ?: BlockchainSdkConfig()
    }

    private val walletManagersThrottler = ThrottlerWithValues<Blockchain, Result<Wallet>>(10000)
    private val fiatRatesThrottler = ThrottlerWithValues<Currency, Result<BigDecimal>?>(60000)

    suspend fun loadWalletData(walletManager: WalletManager) {
        val blockchain = walletManager.wallet.blockchain
        val blockchainNetwork = BlockchainNetwork.fromWalletManager(walletManager)
        val result = if (walletManagersThrottler.isStillThrottled(blockchain)) {
            walletManagersThrottler.geValue(blockchain)!!
        } else {
            updateThrottlingForWalletManager(walletManager)
        }
        when (result) {
            is Result.Success -> {
                checkForRentWarning(walletManager)
                dispatchOnMain(WalletAction.LoadWallet.Success(result.data, blockchainNetwork))
            }
            is Result.Failure -> {
                when (result.error) {
                    is TapError.WalletManagerUpdate.NoAccountError -> {
                        dispatchOnMain(
                            WalletAction.LoadWallet.NoAccount(
                                walletManager.wallet,
                                blockchainNetwork,
                                (result.error as TapError.WalletManagerUpdate.NoAccountError).customMessage
                            )
                        )
                    }
                    else -> {
                        dispatchOnMain(
                            WalletAction.LoadWallet.Failure(
                                walletManager.wallet,
                                result.error.localizedMessage
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun updateThrottlingForWalletManager(walletManager: WalletManager): Result<Wallet> {
        val newResult = walletManager.safeUpdate()
        val blockchain = walletManager.wallet.blockchain
        walletManagersThrottler.updateThrottlingTo(blockchain)
        walletManagersThrottler.setValue(blockchain, newResult)
        return newResult
    }

    suspend fun loadFiatRate(fiatCurrency: FiatCurrencyName, wallet: Wallet) {
        val currencies = wallet.getTokens()
            .map { Currency.Token(it, wallet.blockchain, wallet.publicKey.derivationPath?.rawPath) }
            .plus(Currency.Blockchain(wallet.blockchain, wallet.publicKey.derivationPath?.rawPath))
        loadFiatRate(fiatCurrency, currencies)
    }

    suspend fun loadFiatRate(fiatCurrency: FiatCurrencyName, currencies: List<Currency>) {
        // get and submit previous result of equivalents.
        val throttledResult = currencies.filter { fiatRatesThrottler.isStillThrottled(it) }.map {
            Pair(it, fiatRatesThrottler.geValue(it))
        }
        if (throttledResult.isNotEmpty()) handleFiatRatesResult(throttledResult)

        val toUpdate = currencies.filter { !fiatRatesThrottler.isStillThrottled(it) }
        toUpdate.forEach {
            val result = coinMarketCapService.getRate(it.currencySymbol, fiatCurrency)
            if (result is Result.Success) {
                fiatRatesThrottler.updateThrottlingTo(it)
                fiatRatesThrottler.setValue(it, result)
            }
            handleFiatRatesResult(listOf(it to result))
        }
    }

    suspend fun onCardScanned(data: ScanResponse) {
        walletManagersThrottler.clear()
//        fiatRatesThrottler.clear()
        store.state.globalState.feedbackManager?.infoHolder?.setCardInfo(data)
        updateConfigManager(data)

        withMainContext {
            store.dispatch(WalletAction.ResetState)
            store.dispatch(GlobalAction.SaveScanNoteResponse(data))
            store.dispatch(WalletAction.SetIfTestnetCard(data.card.isTestCard))
            store.dispatch(WalletAction.MultiWallet.SetIsMultiwalletAllowed(data.card.isMultiwalletAllowed))
            loadData(data)
        }
    }

    fun updateConfigManager(data: ScanResponse) {
        val configManager = store.state.globalState.configManager
        val blockchain = data.getBlockchain()
        if (data.card.isStart2Coin) {
            configManager?.turnOff(ConfigManager.isSendingToPayIdEnabled)
            configManager?.turnOff(ConfigManager.isTopUpEnabled)
        } else if (blockchain == Blockchain.Bitcoin
            || data.walletData?.blockchain == Blockchain.Bitcoin.id
        ) {
            configManager?.resetToDefault(ConfigManager.isSendingToPayIdEnabled)
            configManager?.resetToDefault(ConfigManager.isTopUpEnabled)
        } else {
            configManager?.resetToDefault(ConfigManager.isSendingToPayIdEnabled)
            configManager?.resetToDefault(ConfigManager.isTopUpEnabled)
        }
    }

    suspend fun loadData(data: ScanResponse) {
        dispatchOnMain(WalletAction.LoadCardInfo(data.card))
        getActionIfUnknownBlockchainOrEmptyWallet(data)?.let {
            dispatchOnMain(it)
            return
        }

        val blockchain = data.getBlockchain()
        val primaryWalletManager = walletManagerFactory.makePrimaryWalletManager(data)

        if (blockchain != Blockchain.Unknown && primaryWalletManager != null) {
            val primaryToken = data.getPrimaryToken()

            dispatchOnMain(WalletAction.MultiWallet.SetPrimaryBlockchain(blockchain))
            if (primaryToken != null) {
                primaryWalletManager.addToken(primaryToken)
                dispatchOnMain(WalletAction.MultiWallet.SetPrimaryToken(primaryToken))
            }
            if (data.card.isMultiwalletAllowed) {
                loadMultiWalletData(data, blockchain, primaryWalletManager)
            } else {
                dispatchOnMain(
                    WalletAction.MultiWallet.AddBlockchains(
                        listOf(BlockchainNetwork.fromWalletManager(primaryWalletManager)),
                        listOf(primaryWalletManager)
                    )
                )
            }
        } else {
            if (data.card.isMultiwalletAllowed) {
                loadMultiWalletData(data, blockchain, null)
            }
        }
        dispatchOnMain(
            WalletAction.LoadWallet(),
            WalletAction.LoadFiatRate()
        )
    }

    private suspend fun loadMultiWalletData(
        scanResponse: ScanResponse,
        primaryBlockchain: Blockchain?,
        primaryWalletManager: WalletManager?
    ) {
        val primaryTokens = primaryWalletManager?.cardTokens?.toList() ?: emptyList()
        val savedCurrencies = currenciesRepository.loadSavedCurrencies(scanResponse.card.cardId, scanResponse.card.derivationStyle)

        if (savedCurrencies.isEmpty()) {
            if (primaryBlockchain != null && primaryWalletManager != null) {
                val blockchainNetwork = BlockchainNetwork.fromWalletManager(primaryWalletManager)
                dispatchOnMain(
                    WalletAction.MultiWallet.SaveCurrencies(listOf(blockchainNetwork)),
                    WalletAction.MultiWallet.AddBlockchains(
                        listOf(blockchainNetwork),
                        listOf(primaryWalletManager)
                    ),
                    WalletAction.MultiWallet.AddTokens(primaryTokens.toList(), blockchainNetwork)
                )

            } else {
                val derivationStyle = scanResponse.card.derivationStyle
                val blockchainNetworks = listOf(
                    BlockchainNetwork(Blockchain.Bitcoin, scanResponse.card),
                    BlockchainNetwork(Blockchain.Ethereum, scanResponse.card)
                )

                val walletManagers = walletManagerFactory.makeWalletManagersForApp(
                    scanResponse,
                    blockchainNetworks
                )

                dispatchOnMain(
                    WalletAction.MultiWallet.SaveCurrencies(blockchainNetworks),
                    WalletAction.MultiWallet.AddBlockchains(blockchainNetworks, walletManagers),
                )
            }
            dispatchOnMain(
                WalletAction.MultiWallet.FindBlockchainsInUse,
                WalletAction.MultiWallet.FindTokensInUse,
            )
        } else {
            val walletManagers = if (
                primaryTokens.isNotEmpty() &&
                primaryWalletManager != null &&
                primaryBlockchain != null
            ) {
                val blockchainsWithoutPrimary = savedCurrencies.filterNot { it.blockchain == primaryBlockchain }
                walletManagerFactory.makeWalletManagersForApp(
                    scanResponse,
                    blockchainsWithoutPrimary
                ).plus(primaryWalletManager)
            } else {
                walletManagerFactory.makeWalletManagersForApp(scanResponse, savedCurrencies)
            }
            dispatchOnMain(
                WalletAction.MultiWallet.AddBlockchains(savedCurrencies, walletManagers),
            )
            savedCurrencies.map {
                dispatchOnMain(WalletAction.MultiWallet.AddTokens(it.tokens, it))
            }
        }
    }

    suspend fun reloadData(data: ScanResponse) {
        withContext(Dispatchers.Main) {
            getActionIfUnknownBlockchainOrEmptyWallet(data)?.let {
                store.dispatch(it)
                return@withContext
            }
            if (!NetworkConnectivity.getInstance().isOnlineOrConnecting()) {
                store.dispatch(WalletAction.LoadData.Failure(TapError.NoInternetConnection))
                return@withContext
            }

            store.dispatch(WalletAction.LoadWallet())
            store.dispatch(WalletAction.LoadFiatRate())
        }
    }

    private fun getActionIfUnknownBlockchainOrEmptyWallet(data: ScanResponse): WalletAction? {
        return when {
            // check order is important
            data.isTangemTwins() && !data.twinsIsTwinned() -> {
                WalletAction.EmptyWallet
            }
            data.getBlockchain() == Blockchain.Unknown && !data.card.isMultiwalletAllowed -> {
                WalletAction.LoadData.Failure(TapError.UnknownBlockchain)
            }
            data.isDemoCard() -> {
                return null
            }
            data.card.wallets.isEmpty() -> {
                WalletAction.EmptyWallet
            }
            else -> null
        }
    }

    private suspend fun checkForRentWarning(walletManager: WalletManager) {
        val rentProvider = walletManager as? RentProvider ?: return

        when (val result = rentProvider.minimalBalanceForRentExemption()) {
            is com.tangem.blockchain.extensions.Result.Success -> {
                fun isNeedToShowWarning(balance: BigDecimal, rentExempt: BigDecimal): Boolean =
                    balance < rentExempt

                val balance = walletManager.wallet.fundsAvailable(AmountType.Coin)
                val outgoingTxs =
                    walletManager.wallet.getPendingTransactions(PendingTransactionType.Outgoing)
                val rentExempt = result.data
                val show = if (outgoingTxs.isEmpty()) {
                    isNeedToShowWarning(balance, rentExempt)
                } else {
                    val outgoingAmount = outgoingTxs.sumOf { it.amount ?: BigDecimal.ZERO }
                    val rest = balance.minus(outgoingAmount)
                    isNeedToShowWarning(rest, rentExempt)
                }
                if (!show) return

                val currency = walletManager.wallet.blockchain.currency
                dispatchOnMain(
                    WalletAction.SetWalletRent(
                        blockchain = BlockchainNetwork.fromWalletManager(walletManager),
                        minRent = ("${rentProvider.rentAmount().stripZeroPlainString()} $currency"),
                        rentExempt = ("${rentExempt.stripZeroPlainString()} $currency")
                    )
                )
            }
            is com.tangem.blockchain.extensions.Result.Failure -> {}
        }
    }

    private suspend fun handleFiatRatesResult(results: List<Pair<Currency, Result<BigDecimal>?>>) {
        results.map {
            when (it.second) {
                is Result.Success -> {
                    val rate = it.first to (it.second as Result.Success<BigDecimal>).data
                    dispatchOnMain(WalletAction.LoadFiatRate.Success(rate))
                }
                is Result.Failure -> dispatchOnMain(WalletAction.LoadFiatRate.Failure)
                null -> {}
            }
        }
    }
}

fun Wallet.getFirstToken(): Token? {
    return getTokens().toList().getOrNull(0)
}