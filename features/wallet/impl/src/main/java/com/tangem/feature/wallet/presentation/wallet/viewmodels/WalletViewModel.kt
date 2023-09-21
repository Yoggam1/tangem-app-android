package com.tangem.feature.wallet.presentation.wallet.viewmodels

import androidx.lifecycle.*
import androidx.paging.cachedIn
import arrow.core.getOrElse
import com.tangem.blockchain.blockchains.cardano.CardanoUtils
import com.tangem.blockchain.common.Blockchain
import com.tangem.blockchain.common.address.AddressType
import com.tangem.common.Provider
import com.tangem.common.card.EllipticCurve
import com.tangem.common.doOnFailure
import com.tangem.common.doOnSuccess
import com.tangem.common.extensions.ByteArrayKey
import com.tangem.common.extensions.toMapKey
import com.tangem.core.analytics.api.AnalyticsEventHandler
import com.tangem.core.navigation.AppScreen
import com.tangem.core.ui.components.bottomsheets.tokenreceive.AddressModel
import com.tangem.core.ui.components.bottomsheets.tokenreceive.TokenReceiveBottomSheetConfig
import com.tangem.core.ui.components.marketprice.MarketPriceBlockState
import com.tangem.core.ui.extensions.resourceReference
import com.tangem.crypto.hdWallet.DerivationPath
import com.tangem.domain.appcurrency.GetSelectedAppCurrencyUseCase
import com.tangem.domain.appcurrency.model.AppCurrency
import com.tangem.domain.balancehiding.IsBalanceHiddenUseCase
import com.tangem.domain.balancehiding.ListenToFlipsUseCase
import com.tangem.domain.card.*
import com.tangem.domain.common.CardTypesResolver
import com.tangem.domain.common.configs.CardConfig
import com.tangem.domain.common.util.cardTypesResolver
import com.tangem.domain.common.util.derivationStyleProvider
import com.tangem.domain.demo.IsDemoCardUseCase
import com.tangem.domain.models.scan.ScanResponse
import com.tangem.domain.redux.ReduxStateHolder
import com.tangem.domain.settings.CanUseBiometryUseCase
import com.tangem.domain.settings.IsUserAlreadyRateAppUseCase
import com.tangem.domain.settings.ShouldShowSaveWalletScreenUseCase
import com.tangem.domain.tokens.*
import com.tangem.domain.tokens.legacy.TradeCryptoAction
import com.tangem.domain.tokens.model.CryptoCurrencyStatus
import com.tangem.domain.tokens.model.NetworkGroup
import com.tangem.domain.tokens.model.TokenList
import com.tangem.domain.tokens.models.CryptoCurrency
import com.tangem.domain.txhistory.usecase.GetTxHistoryItemsCountUseCase
import com.tangem.domain.txhistory.usecase.GetTxHistoryItemsUseCase
import com.tangem.domain.userwallets.UserWalletBuilder
import com.tangem.domain.walletmanager.WalletManagersFacade
import com.tangem.domain.wallets.models.UserWallet
import com.tangem.domain.wallets.models.UserWalletId
import com.tangem.domain.wallets.usecase.*
import com.tangem.feature.wallet.impl.R
import com.tangem.feature.wallet.presentation.common.state.TokenItemState
import com.tangem.feature.wallet.presentation.router.InnerWalletRouter
import com.tangem.feature.wallet.presentation.wallet.analytics.PortfolioEvent
import com.tangem.feature.wallet.presentation.wallet.analytics.WalletScreenAnalyticsEvent
import com.tangem.feature.wallet.presentation.wallet.state.*
import com.tangem.feature.wallet.presentation.wallet.state.components.WalletCardState
import com.tangem.feature.wallet.presentation.wallet.state.components.WalletTokensListState
import com.tangem.feature.wallet.presentation.wallet.state.factory.WalletStateFactory
import com.tangem.operations.derivation.ExtendedPublicKeysMap
import com.tangem.utils.coroutines.CoroutineDispatcherProvider
import com.tangem.utils.coroutines.JobHolder
import com.tangem.utils.coroutines.saveIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Wallet screen view model
 *
 * @author Andrew Khokhlov on 31/05/2023
 */
@Suppress("LargeClass", "LongParameterList", "TooManyFunctions")
@HiltViewModel
internal class WalletViewModel @Inject constructor(
    // region Parameters
    private val getWalletsUseCase: GetWalletsUseCase,
    private val saveWalletUseCase: SaveWalletUseCase,
    getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val selectWalletUseCase: SelectWalletUseCase,
    private val updateWalletUseCase: UpdateWalletUseCase,
    private val deleteWalletUseCase: DeleteWalletUseCase,
    private val getBiometricsStatusUseCase: GetBiometricsStatusUseCase,
    private val setAccessCodeRequestPolicyUseCase: SetAccessCodeRequestPolicyUseCase,
    private val getAccessCodeSavingStatusUseCase: GetAccessCodeSavingStatusUseCase,
    private val getTokenListUseCase: GetTokenListUseCase,
    private val fetchTokenListUseCase: FetchTokenListUseCase,
    private val getPrimaryCurrencyStatusUpdatesUseCase: GetPrimaryCurrencyStatusUpdatesUseCase,
    private val fetchCurrencyStatusUseCase: FetchCurrencyStatusUseCase,
    private val getNetworkCoinStatusUseCase: GetNetworkCoinStatusUseCase,
    private val scanCardProcessor: ScanCardProcessor,
    private val derivePublicKeysUseCase: DerivePublicKeysUseCase,
    private val txHistoryItemsCountUseCase: GetTxHistoryItemsCountUseCase,
    private val txHistoryItemsUseCase: GetTxHistoryItemsUseCase,
    private val getExploreUrlUseCase: GetExploreUrlUseCase,
    private val unlockWalletsUseCase: UnlockWalletsUseCase,
    private val getSelectedAppCurrencyUseCase: GetSelectedAppCurrencyUseCase,
    private val getCryptoCurrencyActionsUseCase: GetCryptoCurrencyActionsUseCase,
    private val shouldShowSaveWalletScreenUseCase: ShouldShowSaveWalletScreenUseCase,
    private val canUseBiometryUseCase: CanUseBiometryUseCase,
    private val shouldSaveUserWalletsUseCase: ShouldSaveUserWalletsUseCase,
    private val isBalanceHiddenUseCase: IsBalanceHiddenUseCase,
    private val listenToFlipsUseCase: ListenToFlipsUseCase,
    private val walletManagersFacade: WalletManagersFacade,
    private val reduxStateHolder: ReduxStateHolder,
    private val dispatchers: CoroutineDispatcherProvider,
    private val analyticsEventsHandler: AnalyticsEventHandler,
    getCardWasScannedUseCase: GetCardWasScannedUseCase,
    isUserAlreadyRateAppUseCase: IsUserAlreadyRateAppUseCase,
    isDemoCardUseCase: IsDemoCardUseCase,
    // endregion Parameters
) : ViewModel(), DefaultLifecycleObserver, WalletClickIntents {

    /** Feature router */
    var router: InnerWalletRouter by Delegates.notNull()

    private val selectedAppCurrencyFlow: StateFlow<AppCurrency> = createSelectedAppCurrencyFlow()
    private var isBalanceHidden = true

    private val notificationsListFactory = WalletNotificationsListFactory(
        getCardWasScannedUseCase = getCardWasScannedUseCase,
        isUserAlreadyRateAppUseCase = isUserAlreadyRateAppUseCase,
        isDemoCardUseCase = isDemoCardUseCase,
        clickIntents = this,
    )

    private val stateFactory = WalletStateFactory(
        currentStateProvider = Provider { uiState },
        currentCardTypeResolverProvider = Provider {
            getCardTypeResolver(
                index = requireNotNull(uiState as? WalletState.ContentState).walletsListConfig.selectedWalletIndex,
            )
        },
        currentWalletProvider = Provider {
            wallets[requireNotNull(uiState as? WalletState.ContentState).walletsListConfig.selectedWalletIndex]
        },
        appCurrencyProvider = Provider(selectedAppCurrencyFlow::value),
        isBalanceHiddenProvider = Provider { isBalanceHidden },
        clickIntents = this,
    )

    /** Screen state */
    var uiState: WalletState by uiStateHolder(initialState = stateFactory.getInitialState())

    private var wallets: List<UserWallet> by Delegates.notNull()
    private var singleWalletCryptoCurrencyStatus: CryptoCurrencyStatus? = null

    private val tokensJobHolder = JobHolder()
    private val marketPriceJobHolder = JobHolder()
    private val buttonsJobHolder = JobHolder()
    private val notificationsJobHolder = JobHolder()
    private val refreshContentJobHolder = JobHolder()
    private val onWalletChangeJobHolder = JobHolder()

    private val walletsUpdateActionResolver = WalletsUpdateActionResolver(
        currentStateProvider = Provider { uiState },
        getSelectedWalletUseCase = getSelectedWalletUseCase,
    )

    override fun onCreate(owner: LifecycleOwner) {
        analyticsEventsHandler.send(WalletScreenAnalyticsEvent.ScreenOpened)

        viewModelScope.launch(dispatchers.main) {
            delay(timeMillis = 1_800)

            if (router.isWalletLastScreen() && shouldShowSaveWalletScreenUseCase() && canUseBiometryUseCase()) {
                router.openSaveUserWalletScreen()
            }
        }

        getWalletsUseCase()
            .flowWithLifecycle(owner.lifecycle)
            .distinctUntilChanged()
            .onEach(::updateWallets)
            .flowOn(dispatchers.io)
            .launchIn(viewModelScope)

        isBalanceHiddenUseCase()
            .flowWithLifecycle(owner.lifecycle)
            .onEach { hidden ->
                isBalanceHidden = hidden
                uiState = stateFactory.getHiddenBalanceState(isBalanceHidden = hidden)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            listenToFlipsUseCase()
                .flowWithLifecycle(owner.lifecycle)
                .collect()
        }
    }

    private fun updateWallets(sourceList: List<UserWallet>) {
        wallets = sourceList

        if (sourceList.isEmpty()) return

        when (val action = walletsUpdateActionResolver.resolve(sourceList)) {
            is WalletsUpdateActionResolver.Action.Initialize -> {
                initializeAndLoadState(selectedWalletIndex = action.selectedWalletIndex)
            }
            is WalletsUpdateActionResolver.Action.UpdateWalletName -> {
                uiState = stateFactory.getStateWithUpdatedWalletName(name = action.name)
            }
            is WalletsUpdateActionResolver.Action.UnlockWallet -> {
                uiState = stateFactory.getUnlockedState(action)

                getContentItemsUpdates(index = action.selectedWalletIndex)
            }
            is WalletsUpdateActionResolver.Action.DeleteWallet -> {
                deleteWalletAndUpdateState(action = action)
            }
            is WalletsUpdateActionResolver.Action.AddWallet -> {
                scrollAndUpdateState(action.selectedWalletIndex)
            }
            is WalletsUpdateActionResolver.Action.Unknown -> Unit
        }
    }

    private fun initializeAndLoadState(selectedWalletIndex: Int) {
        uiState = stateFactory.getSkeletonState(wallets = wallets, selectedWalletIndex = selectedWalletIndex)

        getContentItemsUpdates(index = selectedWalletIndex)
    }

    private fun deleteWalletAndUpdateState(action: WalletsUpdateActionResolver.Action.DeleteWallet) {
        val cacheState = WalletStateCache.getState(userWalletId = action.selectedWalletId)
        if (cacheState != null) {
            uiState = stateFactory.getStateWithoutDeletedWallet(cacheState, action)

            if (cacheState.isLoadingState()) {
                uiState = stateFactory.getStateAndTriggerEvent(
                    state = uiState,
                    event = WalletEvent.ChangeWallet(action.selectedWalletIndex),
                    setUiState = { uiState = it },
                )
                getContentItemsUpdates(action.selectedWalletIndex)
            }
        } else {
            /* It's impossible case because user can delete only visible state, but we support this case */
            scrollAndUpdateState(selectedWalletIndex = action.selectedWalletIndex)
        }
    }

    private fun scrollAndUpdateState(selectedWalletIndex: Int) {
        uiState = stateFactory.getSkeletonState(
            wallets = wallets,
            selectedWalletIndex = selectedWalletIndex,
        )

        uiState = stateFactory.getStateAndTriggerEvent(
            state = uiState,
            event = WalletEvent.ChangeWallet(index = selectedWalletIndex),
            setUiState = { uiState = it },
        )

        getContentItemsUpdates(index = selectedWalletIndex)
    }

    override fun onBackClick() {
        viewModelScope.launch(dispatchers.main) {
            router.popBackStack(screen = if (shouldSaveUserWalletsUseCase()) AppScreen.Welcome else AppScreen.Home)
        }
    }

    override fun onGenerateMissedAddressesClick(missedAddressCurrencies: List<CryptoCurrency>) {
        val state = uiState as? WalletState.ContentState ?: return

        analyticsEventsHandler.send(WalletScreenAnalyticsEvent.NoticeScanYourCardTapped)

        viewModelScope.launch(dispatchers.io) {
            val userWallet = getWallet(index = state.walletsListConfig.selectedWalletIndex)

            deriveMissingCurrencies(
                scanResponse = userWallet.scanResponse,
                currencyList = missedAddressCurrencies,
            ) { scannedCardResponse ->
                updateWalletUseCase(
                    userWalletId = userWallet.walletId,
                    update = { it.copy(scanResponse = scannedCardResponse) },
                )
                    .onRight {
                        fetchTokenListUseCase(userWalletId = it.walletId, refresh = true)
                    }
            }
        }
    }

    // TODO: https://tangem.atlassian.net/browse/AND-4572
    private fun deriveMissingCurrencies(
        scanResponse: ScanResponse,
        currencyList: List<CryptoCurrency>,
        onSuccess: suspend (ScanResponse) -> Unit,
    ) {
        val config = CardConfig.createConfig(scanResponse.card)
        val derivationDataList = currencyList.mapNotNull {
            config.primaryCurve(blockchain = Blockchain.fromId(it.network.id.value))?.let { curve ->
                getNewDerivations(curve, scanResponse, currencyList)
            }
        }

        val derivations = derivationDataList
            .associate(DerivationData::derivations)
            .ifEmpty { return }

        viewModelScope.launch(dispatchers.io) {
            derivePublicKeysUseCase(cardId = null, derivations = derivations)
                .onRight {
                    val newDerivedKeys = it.entries
                    val oldDerivedKeys = scanResponse.derivedKeys

                    val walletKeys = (newDerivedKeys.keys + oldDerivedKeys.keys).toSet()

                    val updatedDerivedKeys = walletKeys.associateWith { walletKey ->
                        val oldDerivations = ExtendedPublicKeysMap(oldDerivedKeys[walletKey] ?: emptyMap())
                        val newDerivations = newDerivedKeys[walletKey] ?: ExtendedPublicKeysMap(emptyMap())
                        ExtendedPublicKeysMap(oldDerivations + newDerivations)
                    }
                    val updatedScanResponse = scanResponse.copy(derivedKeys = updatedDerivedKeys)

                    onSuccess(updatedScanResponse)
                }
        }
    }

    private fun getNewDerivations(
        curve: EllipticCurve,
        scanResponse: ScanResponse,
        currencyList: List<CryptoCurrency>,
    ): DerivationData? {
        val wallet = scanResponse.card.wallets.firstOrNull { it.curve == curve } ?: return null

        val manageTokensCandidates = currencyList
            .map { Blockchain.fromId(it.network.id.value) }
            .distinct()
            .filter { it.getSupportedCurves().contains(curve) }
            .mapNotNull { it.derivationPath(scanResponse.derivationStyleProvider.getDerivationStyle()) }

        val customTokensCandidates = currencyList
            .filter { Blockchain.fromId(it.network.id.value).getSupportedCurves().contains(curve) }
            .mapNotNull { it.network.derivationPath.value }
            .map(::DerivationPath)

        val bothCandidates = (manageTokensCandidates + customTokensCandidates).distinct().toMutableList()
        if (bothCandidates.isEmpty()) return null

        currencyList.find { it is CryptoCurrency.Coin && Blockchain.fromId(it.network.id.value) == Blockchain.Cardano }
            ?.let { currency ->
                currency.network.derivationPath.value?.let {
                    bothCandidates.add(CardanoUtils.extendedDerivationPath(DerivationPath(it)))
                }
            }

        val mapKeyOfWalletPublicKey = wallet.publicKey.toMapKey()
        val alreadyDerivedKeys: ExtendedPublicKeysMap =
            scanResponse.derivedKeys[mapKeyOfWalletPublicKey] ?: ExtendedPublicKeysMap(emptyMap())
        val alreadyDerivedPaths = alreadyDerivedKeys.keys.toList()

        val toDerive = bothCandidates.filterNot { alreadyDerivedPaths.contains(it) }
        if (toDerive.isEmpty()) return null

        return DerivationData(derivations = mapKeyOfWalletPublicKey to toDerive)
    }

    class DerivationData(val derivations: Pair<ByteArrayKey, List<DerivationPath>>)

    override fun onScanToUnlockWalletClick() {
        scanToUpdateSelectedWallet()
    }

    private fun scanToUpdateSelectedWallet(onSuccessSave: suspend (UserWallet) -> Unit = {}) {
        val state = uiState as? WalletState.ContentState ?: return

        val prevRequestPolicyStatus = getBiometricsStatusUseCase()

        // Update access the code policy according access code saving status
        setAccessCodeRequestPolicyUseCase(isBiometricsRequestPolicy = getAccessCodeSavingStatusUseCase())

        viewModelScope.launch(dispatchers.io) {
            scanCardProcessor.scan(
                cardId = getWallet(state.walletsListConfig.selectedWalletIndex).cardId,
                allowsRequestAccessCodeFromRepository = true,
            )
                .doOnSuccess {
                    // If card's public key is null then user wallet will be null
                    val userWallet = UserWalletBuilder(scanResponse = it).build()

                    if (userWallet != null) {
                        saveWalletUseCase(userWallet = userWallet, canOverride = true)
                            .onLeft {
                                // Rollback policy if card saving was failed
                                setAccessCodeRequestPolicyUseCase(prevRequestPolicyStatus)
                            }
                            .onRight { onSuccessSave(userWallet) }
                    } else {
                        // Rollback policy if card saving was failed
                        setAccessCodeRequestPolicyUseCase(prevRequestPolicyStatus)
                    }
                }
                .doOnFailure {
                    // Rollback policy if card scanning was failed
                    setAccessCodeRequestPolicyUseCase(prevRequestPolicyStatus)
                }
        }
    }

    override fun onDetailsClick() = router.openDetailsScreen()

    override fun onBackupCardClick() {
        analyticsEventsHandler.send(WalletScreenAnalyticsEvent.NoticeBackupYourWalletTapped)
        router.openOnboardingScreen()
    }

    override fun onMultiWalletSignedHashesNotificationClick() {
        // TODO: https://tangem.atlassian.net/browse/AND-4627
    }

    override fun onLikeTangemAppClick() {
        // TODO: https://tangem.atlassian.net/browse/AND-4626
    }

    override fun onRateTheAppClick() {
        // TODO: https://tangem.atlassian.net/browse/AND-4626
    }

    override fun onWalletChange(index: Int) {
        analyticsEventsHandler.send(WalletScreenAnalyticsEvent.WalletSwipe)

        val state = uiState as? WalletState.ContentState ?: return
        if (state.walletsListConfig.selectedWalletIndex == index) return

        // Reset the job to avoid a redundant state updating
        onWalletChangeJobHolder.update(null)

        viewModelScope.launch(dispatchers.main) {
            withContext(dispatchers.io) {
                selectWalletUseCase(userWalletId = state.walletsListConfig.wallets[index].id)
            }

            val cacheState = WalletStateCache.getState(userWalletId = state.walletsListConfig.wallets[index].id)
            if (cacheState != null && cacheState !is WalletLockedState) {
                uiState = cacheState.copySealed(
                    walletsListConfig = state.walletsListConfig.copy(
                        selectedWalletIndex = index,
                        wallets = state.walletsListConfig.wallets
                            .mapIndexed { mapIndex, currentWallet ->
                                val cacheWallet = cacheState.walletsListConfig.wallets.getOrNull(mapIndex)

                                if (currentWallet is WalletCardState.Loading && cacheWallet != null &&
                                    cacheWallet.isLoaded()
                                ) {
                                    cacheWallet
                                } else {
                                    currentWallet
                                }
                            }
                            .toImmutableList(),
                    ),
                    pullToRefreshConfig = cacheState.pullToRefreshConfig.copy(isRefreshing = false),
                )

                if (cacheState.isLoadingState()) {
                    getContentItemsUpdates(index)
                }
            } else {
                initializeAndLoadState(selectedWalletIndex = index)
            }
        }
            .saveIn(onWalletChangeJobHolder)
    }

    private fun WalletCardState.isLoaded(): Boolean {
        return this !is WalletCardState.Loading && this !is WalletCardState.LockedContent
    }

    override fun onRefreshSwipe() {
        val selectedWalletIndex = (uiState as? WalletState.ContentState)
            ?.walletsListConfig
            ?.selectedWalletIndex
            ?: return

        when (uiState) {
            is WalletMultiCurrencyState.Content -> {
                analyticsEventsHandler.send(PortfolioEvent.Refreshed)
                refreshMultiCurrencyContent(selectedWalletIndex)
            }
            is WalletSingleCurrencyState.Content -> {
                analyticsEventsHandler.send(PortfolioEvent.Refreshed)
                refreshSingleCurrencyContent(selectedWalletIndex)
            }
            is WalletState.Initial,
            is WalletMultiCurrencyState.Locked,
            is WalletSingleCurrencyState.Locked,
            -> Unit
        }
    }

    override fun onOrganizeTokensClick() {
        analyticsEventsHandler.send(PortfolioEvent.OrganizeTokens)

        val state = requireNotNull(uiState as? WalletState.ContentState)
        val index = state.walletsListConfig.selectedWalletIndex
        val walletId = state.walletsListConfig.wallets[index].id

        router.openOrganizeTokensScreen(walletId)
    }

    override fun onBuyClick(cryptoCurrencyStatus: CryptoCurrencyStatus) {
        val state = uiState as? WalletState.ContentState ?: return
        val wallet = getWallet(index = state.walletsListConfig.selectedWalletIndex)

        reduxStateHolder.dispatch(
            TradeCryptoAction.New.Buy(
                userWallet = wallet,
                cryptoCurrencyStatus = cryptoCurrencyStatus,
                appCurrencyCode = selectedAppCurrencyFlow.value.code,
            ),
        )
    }

    override fun onSwapClick(cryptoCurrencyStatus: CryptoCurrencyStatus) {
        // todo implement onSwapClick https://tangem.atlassian.net/browse/AND-4535
    }

    override fun onSingleCurrencySendClick(cryptoCurrencyStatus: CryptoCurrencyStatus?) {
        val state = uiState as? WalletState.ContentState ?: return

        val userWallet = getWallet(index = state.walletsListConfig.selectedWalletIndex)
        val coinStatus = if (userWallet.isMultiCurrency) cryptoCurrencyStatus else singleWalletCryptoCurrencyStatus

        reduxStateHolder.dispatch(
            action = TradeCryptoAction.New.SendCoin(
                userWallet = userWallet,
                coinStatus = coinStatus ?: return,
            ),
        )
    }

    override fun onMultiCurrencySendClick(cryptoCurrencyStatus: CryptoCurrencyStatus) {
        if (cryptoCurrencyStatus.currency is CryptoCurrency.Coin) {
            onSingleCurrencySendClick(cryptoCurrencyStatus = cryptoCurrencyStatus)
            return
        }

        val state = uiState as? WalletState.ContentState ?: return

        viewModelScope.launch(dispatchers.io) {
            val userWallet = getWallet(index = state.walletsListConfig.selectedWalletIndex)

            getNetworkCoinStatusUseCase(
                userWalletId = userWallet.walletId,
                networkId = cryptoCurrencyStatus.currency.network.id,
            )
                .take(count = 1)
                .collectLatest {
                    it.onRight { coinStatus ->
                        reduxStateHolder.dispatch(
                            action = TradeCryptoAction.New.SendToken(
                                userWallet = getWallet(index = state.walletsListConfig.selectedWalletIndex),
                                tokenStatus = cryptoCurrencyStatus,
                                coinFiatRate = coinStatus.value.fiatRate,
                            ),
                        )
                    }
                }
        }
    }

    override fun onReceiveClick(cryptoCurrencyStatus: CryptoCurrencyStatus) {
        val state = uiState as? WalletState.ContentState ?: return

        viewModelScope.launch(dispatchers.io) {
            val userWallet = getWallet(index = state.walletsListConfig.selectedWalletIndex)

            val addresses = walletManagersFacade.getAddress(
                userWalletId = userWallet.walletId,
                network = cryptoCurrencyStatus.currency.network,
            )

            val currency = cryptoCurrencyStatus.currency
            uiState = stateFactory.getStateWithOpenWalletBottomSheet(
                content = TokenReceiveBottomSheetConfig(
                    name = currency.name,
                    symbol = currency.symbol,
                    network = currency.network.name,
                    addresses = addresses.map {
                        AddressModel(
                            value = it.value,
                            type = AddressModel.Type.valueOf(it.type.name),
                        )
                    },
                ),
            )
        }
    }

    override fun onCopyAddressClick(cryptoCurrencyStatus: CryptoCurrencyStatus) {
        val state = uiState as? WalletState.ContentState ?: return

        viewModelScope.launch(dispatchers.main) {
            val userWallet = getWallet(index = state.walletsListConfig.selectedWalletIndex)

            val defaultAddress = walletManagersFacade.getAddress(
                userWalletId = userWallet.walletId,
                network = cryptoCurrencyStatus.currency.network,
            ).find { it.type == AddressType.Default }

            defaultAddress?.value?.let { address ->
                uiState = stateFactory.getStateAndTriggerEvent(
                    state = uiState,
                    event = WalletEvent.CopyAddress(address),
                    setUiState = { uiState = it },
                )
                uiState = stateFactory.getStateAndTriggerEvent(
                    state = uiState,
                    event = WalletEvent.ShowToast(resourceReference(R.string.wallet_notification_address_copied)),
                    setUiState = { uiState = it },
                )
            }
        }
    }

    override fun onSellClick(cryptoCurrencyStatus: CryptoCurrencyStatus) {
        reduxStateHolder.dispatch(
            TradeCryptoAction.New.Sell(
                cryptoCurrencyStatus = cryptoCurrencyStatus,
                appCurrencyCode = selectedAppCurrencyFlow.value.code,
            ),
        )
    }

    override fun onManageTokensClick() {
        analyticsEventsHandler.send(PortfolioEvent.ButtonManageTokens)
        reduxStateHolder.dispatch(action = TokensAction.SetArgs.ManageAccess)
        router.openManageTokensScreen()
    }

    override fun onReloadClick() {
        val selectedWalletIndex = (uiState as? WalletSingleCurrencyState)
            ?.walletsListConfig
            ?.selectedWalletIndex
            ?: return

        refreshSingleCurrencyContent(selectedWalletIndex)
    }

    override fun onExploreClick() {
        viewModelScope.launch(dispatchers.io) {
            val wallet = getWallet(
                index = requireNotNull(uiState as? WalletState.ContentState).walletsListConfig.selectedWalletIndex,
            )
            val currencyStatus = getPrimaryCurrencyStatusUpdatesUseCase(wallet.walletId)
                .firstOrNull()
                ?.getOrNull()

            if (currencyStatus != null) {
                router.openTxHistoryWebsite(
                    url = getExploreUrlUseCase(
                        userWalletId = wallet.walletId,
                        network = currencyStatus.currency.network,
                    ),
                )
            }
        }
    }

    override fun onUnlockWalletClick() {
        analyticsEventsHandler.send(WalletScreenAnalyticsEvent.NoticeWalletLocked)

        viewModelScope.launch(dispatchers.io) {
            unlockWalletsUseCase()
        }
    }

    override fun onUnlockWalletNotificationClick() {
        val state = requireNotNull(uiState as? WalletLockedState) {
            "Impossible to unlock wallet if state isn't WalletLockedState"
        }

        uiState = stateFactory.getStateWithOpenWalletBottomSheet(
            content = when (state) {
                is WalletMultiCurrencyState.Locked -> state.bottomSheetConfig.content
                is WalletSingleCurrencyState.Locked -> state.bottomSheetConfig.content
            },
        )
    }

    override fun onTokenItemClick(currency: CryptoCurrency) {
        analyticsEventsHandler.send(PortfolioEvent.TokenTapped)
        router.openTokenDetails(currency = currency)
    }

    override fun onTokenItemLongClick(cryptoCurrencyStatus: CryptoCurrencyStatus) {
        val state = uiState as? WalletState.ContentState ?: return
        val userWallet = getWallet(state.walletsListConfig.selectedWalletIndex)
        viewModelScope.launch(dispatchers.io) {
            getCryptoCurrencyActionsUseCase
                .invoke(userWallet.walletId, cryptoCurrencyStatus)
                .take(count = 1)
                .collectLatest {
                    uiState = stateFactory.getStateWithTokenActionBottomSheet(it)
                }
        }
    }

    override fun onRenameClick(userWalletId: UserWalletId, name: String) {
        viewModelScope.launch(dispatchers.io) {
            updateWalletUseCase(userWalletId = userWalletId, update = { it.copy(name) })
        }
    }

    override fun onDeleteClick(userWalletId: UserWalletId) {
        val state = uiState as? WalletState.ContentState ?: return

        viewModelScope.launch(dispatchers.io) {
            val either = deleteWalletUseCase(userWalletId)

            if (state.walletsListConfig.wallets.size <= 1 && either.isRight()) onBackClick()
        }
    }

    override fun onDismissBottomSheet() {
        uiState = stateFactory.getStateWithClosedBottomSheet()
    }

    override fun onDismissActionsBottomSheet() {
        (uiState as? WalletMultiCurrencyState.Content)?.let { state ->
            uiState = state.copy(
                tokenActionsBottomSheet = state.tokenActionsBottomSheet?.copy(
                    isShow = false,
                ),
            )
        }
    }

    private fun getContentItemsUpdates(index: Int) {
        /*
         * When wallet is changed it's necessary to stop the last jobs.
         * If jobs aren't stopped and wallet is changed then it will update state for the prev wallet.
         */
        tokensJobHolder.update(job = null)
        marketPriceJobHolder.update(job = null)
        buttonsJobHolder.update(job = null)
        notificationsJobHolder.update(job = null)
        refreshContentJobHolder.update(job = null)

        val wallet = getWallet(index)

        when {
            wallet.isLocked -> {
                uiState = stateFactory.getLockedState()
            }
            wallet.isMultiCurrency -> getMultiCurrencyContent(index)
            !wallet.isMultiCurrency -> getSingleCurrencyContent(index)
        }
    }

    private fun getMultiCurrencyContent(walletIndex: Int) {
        val state = requireNotNull(uiState as? WalletMultiCurrencyState) {
            "Impossible to get a token list updates if state isn't WalletMultiCurrencyState"
        }

        getTokenListUseCase(userWalletId = state.walletsListConfig.wallets[walletIndex].id)
            .distinctUntilChanged()
            .onEach { maybeTokenList ->
                uiState = stateFactory.getStateByTokensList(maybeTokenList)

                updateNotifications(
                    index = walletIndex,
                    tokenList = maybeTokenList.fold(ifLeft = { null }, ifRight = { it }),
                )
            }
            .flowOn(dispatchers.io)
            .launchIn(viewModelScope)
            .saveIn(tokensJobHolder)
    }

    private fun getSingleCurrencyContent(index: Int) {
        val wallet = getWallet(index)
        updatePrimaryCurrencyStatus(userWalletId = wallet.walletId)
        updateNotifications(index)
    }

    private fun updateTxHistory(currency: CryptoCurrency) {
        viewModelScope.launch(dispatchers.io) {
            val txHistoryItemsCountEither = txHistoryItemsCountUseCase(currency.network)

            uiState = stateFactory.getLoadingTxHistoryState(
                itemsCountEither = txHistoryItemsCountEither,
            )

            txHistoryItemsCountEither.onRight {
                uiState = stateFactory.getLoadedTxHistoryState(
                    txHistoryEither = txHistoryItemsUseCase(currency = currency).map {
                        it.cachedIn(viewModelScope)
                    },
                )
            }
        }
    }

    private fun updatePrimaryCurrencyStatus(userWalletId: UserWalletId) {
        getPrimaryCurrencyStatusUpdatesUseCase(userWalletId = userWalletId)
            .distinctUntilChanged()
            .onEach { maybeCryptoCurrencyStatus ->
                uiState = stateFactory.getSingleCurrencyLoadedBalanceState(maybeCryptoCurrencyStatus)

                maybeCryptoCurrencyStatus.onRight { status ->
                    singleWalletCryptoCurrencyStatus = status
                    updateButtons(userWalletId = userWalletId, currencyStatus = status)
                    updateTxHistory(status.currency)
                }
            }
            .flowOn(dispatchers.io)
            .launchIn(viewModelScope)
            .saveIn(marketPriceJobHolder)
    }

    private fun updateButtons(userWalletId: UserWalletId, currencyStatus: CryptoCurrencyStatus) {
        getCryptoCurrencyActionsUseCase(userWalletId = userWalletId, cryptoCurrencyStatus = currencyStatus)
            .distinctUntilChanged()
            .onEach { uiState = stateFactory.getSingleCurrencyManageButtonsState(actionsState = it) }
            .flowOn(dispatchers.io)
            .launchIn(viewModelScope)
            .saveIn(buttonsJobHolder)
    }

    private fun updateNotifications(index: Int, tokenList: TokenList? = null) {
        notificationsListFactory.create(
            cardTypesResolver = getCardTypeResolver(index = index),
            cryptoCurrencyList = if (tokenList != null) {
                when (tokenList) {
                    is TokenList.GroupedByNetwork -> {
                        tokenList.groups
                            .flatMap(NetworkGroup::currencies)
                    }
                    is TokenList.Ungrouped -> tokenList.currencies
                    is TokenList.NotInitialized -> emptyList()
                }
            } else {
                listOfNotNull(singleWalletCryptoCurrencyStatus)
            },
        )
            .distinctUntilChanged()
            .onEach { uiState = stateFactory.getStateByNotifications(notifications = it) }
            .flowOn(dispatchers.io)
            .launchIn(viewModelScope)
            .saveIn(notificationsJobHolder)
    }

    private fun refreshMultiCurrencyContent(walletIndex: Int) {
        uiState = stateFactory.getRefreshingState()
        val wallet = getWallet(walletIndex)

        viewModelScope.launch(dispatchers.io) {
            val result = fetchTokenListUseCase(wallet.walletId, refresh = true)

            uiState = stateFactory.getRefreshedState()
            uiState = result.fold(stateFactory::getStateByTokenListError) { uiState }
        }.saveIn(refreshContentJobHolder)
    }

    private fun refreshSingleCurrencyContent(walletIndex: Int) {
        uiState = stateFactory.getRefreshingState()
        val wallet = getWallet(walletIndex)

        viewModelScope.launch(dispatchers.io) {
            val result = fetchCurrencyStatusUseCase(wallet.walletId, refresh = true)

            uiState = stateFactory.getRefreshedState()
            uiState = result.fold(stateFactory::getStateByCurrencyStatusError) { uiState }
        }.saveIn(refreshContentJobHolder)
    }

    private fun createSelectedAppCurrencyFlow(): StateFlow<AppCurrency> {
        return getSelectedAppCurrencyUseCase()
            .map { maybeAppCurrency ->
                maybeAppCurrency.getOrElse { AppCurrency.Default }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = AppCurrency.Default,
            )
    }

    private fun WalletState.isLoadingState(): Boolean {
        // Check the base components
        if (this is WalletState.ContentState &&
            walletsListConfig.wallets[walletsListConfig.selectedWalletIndex] is WalletCardState.Loading
        ) {
            return true
        }

        // Check the special components
        return when (this) {
            is WalletMultiCurrencyState -> {
                val hasLoadingTokens = tokensListState is WalletTokensListState.ContentState &&
                    (tokensListState as WalletTokensListState.ContentState).items
                        .filterIsInstance<WalletTokensListState.TokensListItemState.Token>()
                        .any { it.state is TokenItemState.Loading }

                tokensListState is WalletTokensListState.Loading || hasLoadingTokens
            }
            is WalletSingleCurrencyState -> {
                this is WalletSingleCurrencyState.Content && marketPriceBlockState is MarketPriceBlockState.Loading
            }
            is WalletState.Initial -> false
        }
    }

    private fun getWallet(index: Int): UserWallet {
        return requireNotNull(
            value = wallets.getOrNull(index),
            lazyMessage = { "WalletsList doesn't contain element with index = $index" },
        )
    }

    private fun getCardTypeResolver(index: Int): CardTypesResolver = getWallet(index).scanResponse.cardTypesResolver
}
