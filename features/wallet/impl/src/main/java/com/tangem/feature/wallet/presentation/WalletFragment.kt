package com.tangem.feature.wallet.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tangem.core.ui.UiDependencies
import com.tangem.core.ui.components.SystemBarsEffect
import com.tangem.core.ui.res.TangemTheme
import com.tangem.core.ui.screen.ComposeFragment
import com.tangem.feature.wallet.presentation.router.InnerWalletRouter
import com.tangem.features.managetokens.navigation.ManageTokensUi
import com.tangem.features.wallet.navigation.WalletRouter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Wallet fragment
 *
 * @author Andrew Khokhlov on 29/05/2023
 */
@AndroidEntryPoint
internal class WalletFragment : ComposeFragment() {

    @Inject
    override lateinit var uiDependencies: UiDependencies

    @Inject
    internal lateinit var manageTokensUi: ManageTokensUi

    /** Feature router */
    @Inject
    internal lateinit var walletRouter: WalletRouter

    private val _walletRouter: InnerWalletRouter
        get() = requireNotNull(walletRouter as? InnerWalletRouter) {
            "_walletRouter should be instance of InnerWalletRouter"
        }

    @Composable
    override fun ScreenContent(modifier: Modifier) {
        val systemBarsColor = TangemTheme.colors.background.secondary
        SystemBarsEffect {
            setSystemBarsColor(systemBarsColor)
        }

        _walletRouter.Initialize(
            onFinish = requireActivity()::finish,
            manageTokensUi = manageTokensUi,
        )
    }

    companion object {

        /** Create wallet fragment instance */
        fun create(): WalletFragment = WalletFragment()
    }
}
