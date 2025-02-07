package com.tangem.features.disclaimer.impl.presentation.ui

import android.graphics.Bitmap
import android.webkit.*
import androidx.compose.runtime.MutableState

internal enum class ProgressState {
    Loading,
    Done,
    Error,
}

internal class DisclaimerWebViewClient(private val progressState: MutableState<ProgressState>) : WebViewClient() {

    private var loadingUrl: String? = null
    private var loadedUrl: String? = null

    fun reset() {
        loadingUrl = null
        loadedUrl = null
        progressState.value = ProgressState.Loading
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        if (loadingUrl != url) progressState.value = ProgressState.Loading
        loadingUrl = url
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        if (loadedUrl != url) progressState.value = ProgressState.Done
        loadedUrl = url
    }

    override fun onReceivedError(view: WebView?, resourceRequest: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, resourceRequest, error)
        error?.let { progressState.value = ProgressState.Error }
    }

    override fun onReceivedHttpError(
        view: WebView?,
        resourceRequest: WebResourceRequest?,
        errorResponse: WebResourceResponse?,
    ) {
        super.onReceivedHttpError(view, resourceRequest, errorResponse)

        if (resourceRequest != null && errorResponse != null) {
            val isDifferentUrl = resourceRequest.url?.toString() != loadingUrl
            val isSuccessCode = errorResponse.statusCode < RESPONSE_USER_ERROR_STATUS_CODE
            val isNotDone = progressState.value != ProgressState.Done
            if (isDifferentUrl || isSuccessCode || isNotDone) return
            progressState.value = ProgressState.Error
        }
    }

    companion object {
        private const val RESPONSE_USER_ERROR_STATUS_CODE = 400
    }
}
