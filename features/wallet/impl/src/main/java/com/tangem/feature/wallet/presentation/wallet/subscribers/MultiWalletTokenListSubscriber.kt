package com.tangem.feature.wallet.presentation.wallet.subscribers

import com.tangem.domain.appcurrency.GetSelectedAppCurrencyUseCase
import com.tangem.domain.core.lce.Lce
import com.tangem.domain.core.lce.LceFlow
import com.tangem.domain.core.utils.toLce
import com.tangem.domain.tokens.ApplyTokenListSortingUseCase
import com.tangem.domain.tokens.GetTokenListUseCase
import com.tangem.domain.tokens.RunPolkadotAccountHealthCheckUseCase
import com.tangem.domain.tokens.error.TokenListError
import com.tangem.domain.tokens.model.CryptoCurrency
import com.tangem.domain.tokens.model.TokenList
import com.tangem.domain.tokens.model.TotalFiatBalance
import com.tangem.domain.wallets.models.UserWallet
import com.tangem.feature.wallet.featuretoggle.WalletFeatureToggles
import com.tangem.feature.wallet.presentation.wallet.analytics.utils.TokenListAnalyticsSender
import com.tangem.feature.wallet.presentation.wallet.domain.WalletWithFundsChecker
import com.tangem.feature.wallet.presentation.wallet.state.WalletStateController
import com.tangem.feature.wallet.presentation.wallet.viewmodels.intents.WalletClickIntents
import kotlinx.coroutines.flow.map

@Suppress("LongParameterList")
internal class MultiWalletTokenListSubscriber(
    private val userWallet: UserWallet,
    private val getTokenListUseCase: GetTokenListUseCase,
    private val applyTokenListSortingUseCase: ApplyTokenListSortingUseCase,
    private val walletFeatureToggles: WalletFeatureToggles,
    stateHolder: WalletStateController,
    clickIntents: WalletClickIntents,
    tokenListAnalyticsSender: TokenListAnalyticsSender,
    walletWithFundsChecker: WalletWithFundsChecker,
    getSelectedAppCurrencyUseCase: GetSelectedAppCurrencyUseCase,
    runPolkadotAccountHealthCheckUseCase: RunPolkadotAccountHealthCheckUseCase,
) : BasicTokenListSubscriber(
    userWallet = userWallet,
    stateHolder = stateHolder,
    clickIntents = clickIntents,
    tokenListAnalyticsSender = tokenListAnalyticsSender,
    walletWithFundsChecker = walletWithFundsChecker,
    getSelectedAppCurrencyUseCase = getSelectedAppCurrencyUseCase,
    runPolkadotAccountHealthCheckUseCase = runPolkadotAccountHealthCheckUseCase,
) {

    override fun tokenListFlow(): LceFlow<TokenListError, TokenList> {
        return if (walletFeatureToggles.isTokenListLceFlowEnabled) {
            getTokenListUseCase.launchLce(userWallet.walletId)
        } else {
            getTokenListUseCase.launch(userWallet.walletId).map { it.toLce() }
        }
    }

    override suspend fun onTokenListReceived(maybeTokenList: Lce<TokenListError, TokenList>) {
        updateSortingIfNeeded(maybeTokenList)
    }

    private suspend fun updateSortingIfNeeded(maybeTokenList: Lce<TokenListError, TokenList>) {
        val tokenList = maybeTokenList.getOrNull() ?: return
        if (!checkNeedSorting(tokenList)) return

        applyTokenListSortingUseCase(
            userWalletId = userWallet.walletId,
            sortedTokensIds = getCurrenciesIds(tokenList),
            isGroupedByNetwork = tokenList is TokenList.GroupedByNetwork,
            isSortedByBalance = tokenList.sortedBy == TokenList.SortType.BALANCE,
        )
    }

    private fun checkNeedSorting(tokenList: TokenList): Boolean {
        return tokenList.totalFiatBalance !is TotalFiatBalance.Loading &&
            tokenList.sortedBy == TokenList.SortType.BALANCE
    }

    private fun getCurrenciesIds(tokenList: TokenList): List<CryptoCurrency.ID> {
        return when (tokenList) {
            is TokenList.GroupedByNetwork -> tokenList.groups.flatMap { group ->
                group.currencies.map { it.currency.id }
            }
            is TokenList.Ungrouped -> tokenList.currencies.map { it.currency.id }
            is TokenList.Empty -> emptyList()
        }
    }
}
