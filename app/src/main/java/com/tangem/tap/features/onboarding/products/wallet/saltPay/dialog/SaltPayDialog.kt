package com.tangem.tap.features.onboarding.products.wallet.saltPay.dialog

import com.tangem.common.extensions.VoidCallback
import com.tangem.tap.common.redux.StateDialog
import com.tangem.tap.features.onboarding.products.wallet.saltPay.message.SaltPayRegistrationError

/**
 * Created by Anton Zhilenkov on 12.10.2022.
 */
sealed class SaltPayDialog : StateDialog {
    sealed class Activation : SaltPayDialog() {
        object NoFunds : SaltPayDialog()
        data class OnError(val error: SaltPayRegistrationError) : SaltPayDialog()
        data class TryToInterrupt(val onOk: VoidCallback, val onCancel: VoidCallback) : SaltPayDialog()
    }
}
