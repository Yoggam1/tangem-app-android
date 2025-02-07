package com.tangem.tap.common.feedback

import android.content.Context
import com.tangem.core.navigation.email.EmailSender
import com.tangem.datasource.config.models.ChatConfig
import com.tangem.domain.common.TapWorkarounds
import com.tangem.domain.feedback.FeedbackManagerFeatureToggles
import com.tangem.domain.feedback.GetFeedbackEmailUseCase
import com.tangem.domain.feedback.models.FeedbackEmailType
import com.tangem.tap.common.chat.ChatManager
import com.tangem.tap.common.extensions.inject
import com.tangem.tap.common.extensions.sendEmail
import com.tangem.tap.common.log.TangemLogCollector
import com.tangem.tap.foregroundActivityObserver
import com.tangem.tap.mainScope
import com.tangem.tap.proxy.redux.DaggerGraphState
import com.tangem.tap.store
import com.tangem.tap.withForegroundActivity
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

/**
 * Created by Anton Zhilenkov on 25/02/2021.
 */
class LegacyFeedbackManager(
    val infoHolder: AdditionalFeedbackInfo,
    private val logCollector: TangemLogCollector,
    private val chatManager: ChatManager,
    private val feedbackManagerFeatureToggles: FeedbackManagerFeatureToggles,
    private val getFeedbackEmailUseCase: GetFeedbackEmailUseCase,
) {

    private var sessionFeedbackFile: File? = null
    private var sessionLogsFile: File? = null

    fun sendEmail(feedbackData: FeedbackData, onFail: ((Exception) -> Unit)? = null) {
        if (feedbackManagerFeatureToggles.isLocalLogsEnabled) {
            mainScope.launch {
                val email = getFeedbackEmailUseCase(
                    when (feedbackData) {
                        is FeedbackEmail -> FeedbackEmailType.DirectUserRequest
                        is RateCanBeBetterEmail -> FeedbackEmailType.RateCanBeBetter
                        is ScanFailsEmail -> FeedbackEmailType.ScanningProblem
                        is SendTransactionFailedEmail -> FeedbackEmailType.TransactionSendingProblem
                        else -> FeedbackEmailType.DirectUserRequest
                    },
                )

                store.inject(DaggerGraphState::emailSender).send(
                    email = EmailSender.Email(
                        address = email.address,
                        subject = email.subject,
                        message = email.message,
                        attachment = email.file,
                    ),
                )
            }
        } else {
            feedbackData.prepare(infoHolder)
            foregroundActivityObserver.withForegroundActivity { activity ->
                activity.sendEmail(
                    email = getSupportEmail(),
                    subject = activity.getString(feedbackData.subjectResId),
                    message = feedbackData.joinTogether(activity, infoHolder),
                    file = getLogFile(activity),
                    onFail = onFail,
                )
            }
        }
    }

    fun openChat(config: ChatConfig, feedbackData: FeedbackData) {
        chatManager.open(
            config = config,
            createLogsFile = ::getLogFile,
            createFeedbackFile = { context -> getFeedbackFile(context, feedbackData) },
        )
    }

    private fun getFeedbackFile(context: Context, feedbackData: FeedbackData): File? {
        return try {
            if (sessionFeedbackFile != null) {
                return sessionFeedbackFile
            }
            val file = File(context.filesDir, FEEDBACK_FILE)
            file.delete()
            file.createNewFile()

            val feedback = feedbackData.run {
                prepare(infoHolder)
                joinTogether(context, infoHolder)
            }
            val fileWriter = FileWriter(file)
            fileWriter.write(feedback)
            fileWriter.close()

            if (file.exists()) {
                sessionFeedbackFile = file
                sessionFeedbackFile
            } else {
                null
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Can't create the logs file")
            null
        }
    }

    private fun getLogFile(context: Context): File? {
        return try {
            if (sessionLogsFile != null) {
                return sessionLogsFile
            }
            val file = File(context.filesDir, LOGS_FILE)
            file.delete()
            file.createNewFile()

            val stringWriter = StringWriter()
            logCollector.getLogs().forEach { stringWriter.append(it) }
            val fileWriter = FileWriter(file)
            fileWriter.write(stringWriter.toString())
            fileWriter.close()
            logCollector.clearLogs()
            if (file.exists()) {
                sessionLogsFile = file
                sessionLogsFile
            } else {
                null
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Can't create the logs file")
            null
        }
    }

    private fun getSupportEmail(): String {
        return if (TapWorkarounds.isStart2CoinIssuer(infoHolder.cardIssuer)) {
            S2C_SUPPORT_EMAIL
        } else {
            DEFAULT_SUPPORT_EMAIL
        }
    }

    private companion object {
        const val DEFAULT_SUPPORT_EMAIL = "support@tangem.com"
        const val S2C_SUPPORT_EMAIL = "cardsupport@start2coin.com"
        const val FEEDBACK_FILE = "feedback.txt"
        const val LOGS_FILE = "logs.txt"
    }
}
