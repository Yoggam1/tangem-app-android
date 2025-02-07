package com.tangem.features.send.impl.presentation.state.amount

import androidx.compose.ui.text.input.ImeAction
import com.tangem.common.extensions.isZero
import com.tangem.core.ui.utils.parseBigDecimal
import com.tangem.core.ui.utils.parseToBigDecimal
import com.tangem.domain.tokens.model.CryptoCurrencyStatus
import com.tangem.features.send.impl.presentation.state.fields.SendTextField
import java.math.BigDecimal
import java.math.RoundingMode

internal fun String.getCryptoValue(fiatRate: BigDecimal?, isFiatValue: Boolean, decimals: Int): String {
    return if (isFiatValue && fiatRate != null) {
        parseToBigDecimal(decimals).divide(fiatRate, decimals, RoundingMode.DOWN)
            .parseBigDecimal(decimals)
    } else {
        this
    }
}

internal fun String.getFiatValue(
    fiatRate: BigDecimal?,
    isFiatValue: Boolean,
    decimals: Int,
): Pair<String, BigDecimal?> {
    return if (fiatRate != null) {
        val fiatValue = if (!isFiatValue) {
            parseToBigDecimal(decimals).multiply(fiatRate).parseBigDecimal(decimals)
        } else {
            this
        }
        val decimalFiatValue = fiatValue.parseToBigDecimal(decimals)
        fiatValue to decimalFiatValue
    } else {
        "" to null
    }
}

internal fun String.checkExceedBalance(
    cryptoCurrencyStatus: CryptoCurrencyStatus,
    amountTextField: SendTextField.AmountField,
): Boolean {
    val currencyCryptoAmount = cryptoCurrencyStatus.value.amount ?: BigDecimal.ZERO
    val currencyFiatAmount = cryptoCurrencyStatus.value.fiatAmount ?: BigDecimal.ZERO
    val fiatDecimal = parseToBigDecimal(amountTextField.fiatAmount.decimals)
    val cryptoDecimal = parseToBigDecimal(amountTextField.cryptoAmount.decimals)
    return if (amountTextField.isFiatValue) {
        fiatDecimal > currencyFiatAmount
    } else {
        cryptoDecimal > currencyCryptoAmount
    }
}

internal fun getKeyboardAction(isExceedBalance: Boolean, decimalCryptoValue: BigDecimal) =
    if (!isExceedBalance && !decimalCryptoValue.isZero()) {
        ImeAction.Done
    } else {
        ImeAction.None
    }
