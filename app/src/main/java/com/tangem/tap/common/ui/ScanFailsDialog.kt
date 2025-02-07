package com.tangem.tap.common.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tangem.core.analytics.Analytics
import com.tangem.core.analytics.models.AnalyticsParam
import com.tangem.core.analytics.models.Basic
import com.tangem.domain.redux.StateDialog
import com.tangem.tap.common.extensions.dispatchDialogHide
import com.tangem.tap.common.feedback.ScanFailsEmail
import com.tangem.tap.common.redux.global.GlobalAction
import com.tangem.tap.store
import com.tangem.wallet.R

/**
 * Created by Anton Zhilenkov on 28/02/2021.
 */
object ScanFailsDialog {
    fun create(context: Context, source: StateDialog.ScanFailsSource): AlertDialog {
        return MaterialAlertDialogBuilder(context, R.style.CustomMaterialDialog).apply {
            setTitle(context.getString(R.string.common_warning))
            setMessage(R.string.alert_troubleshooting_scan_card_title)
            setPositiveButton(R.string.alert_button_request_support) { _, _ ->
                val sourceAnalytics = when (source) {
                    StateDialog.ScanFailsSource.MAIN -> AnalyticsParam.ScreensSources.Main
                    StateDialog.ScanFailsSource.SIGN_IN -> AnalyticsParam.ScreensSources.SignIn
                    StateDialog.ScanFailsSource.SETTINGS -> AnalyticsParam.ScreensSources.Settings
                    StateDialog.ScanFailsSource.INTRO -> AnalyticsParam.ScreensSources.Intro
                }
                Analytics.send(Basic.ButtonSupport(sourceAnalytics))
                store.dispatch(GlobalAction.SendEmail(ScanFailsEmail()))
            }
            setNeutralButton(R.string.common_cancel) { _, _ -> }
            setOnDismissListener { store.dispatchDialogHide() }
        }.create()
    }
}
