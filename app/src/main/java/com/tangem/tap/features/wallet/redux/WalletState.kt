package com.tangem.tap.features.wallet.redux

import android.graphics.Bitmap
import com.tangem.blockchain.common.Wallet
import com.tangem.tap.common.entities.Button
import com.tangem.tap.features.wallet.ui.BalanceWidgetData
import org.rekotlin.StateType

data class WalletState(
        val state: ProgressState = ProgressState.Done,
        val cardImage: Bitmap? = null,
        val wallet: Wallet? = null,
        val currencyData: BalanceWidgetData = BalanceWidgetData(),
        val payIdData: PayIdData = PayIdData(),
        val qrCode: Bitmap? = null,
        val creatingPayIdState: CreatingPayIdState? = null,
        val mainButton: WalletMainButton = WalletMainButton.SendButton(false)
) : StateType


enum class ProgressState { Loading, Done, Error }

enum class PayIdState { Disabled, Loading, NotCreated, Created, ErrorLoading }

data class PayIdData(
        val payIdState: PayIdState = PayIdState.Loading,
        val payId: String? = null
)

enum class CreatingPayIdState { EnterPayId, Waiting }

sealed class WalletMainButton(enabled: Boolean) : Button(enabled) {
    class SendButton(enabled: Boolean) : WalletMainButton(enabled)
    class CreateWalletButton(enabled: Boolean) : WalletMainButton(enabled)
}