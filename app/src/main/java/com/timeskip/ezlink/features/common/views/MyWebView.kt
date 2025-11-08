package com.timeskip.ezlink.features.common.views

import android.graphics.Bitmap
import android.graphics.Color
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.timeskip.ezlink.features.common.ConnectivityUtils
import com.timeskip.ezlink.features.link.editor.WebViewError
import kotlinx.coroutines.delay

@Composable
fun WebViewWithTimeout(
    url: String,
    modifier: Modifier = Modifier,
    webViewError: WebViewError? = null,
    webContent: String? = null,
    timeoutMs: Long = 15_000L,
    updateWebViewError: (WebViewError) -> Unit,
    updateContent: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var didTimeout by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(key1 = isLoading) {
        if (isLoading) {
            delay(timeoutMs)

            if (isLoading) {
                didTimeout = true
                isLoading = false
                webViewInstance?.stopLoading() // Stop the page load
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (webViewError != null) {
            GrayLogoWithTextView(
                modifier = Modifier.fillMaxSize(),
                textContent = webViewError?.description.orEmpty()
            )
            return
        }

        // AndroidView is the standard way to host a classic Android View
        AndroidView(
            factory = { context ->
                // Create the WebView instance
                WebView(context).apply {
                    // Set a transparent background
                    setBackgroundColor(Color.TRANSPARENT)
                    // Set up the WebViewClient
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            view?.loadUrl(url)
                            return true
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            // Page loading has started
                            isLoading = true
                            didTimeout = false // Reset timeout on new page load
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Page loading has finished
                            isLoading = false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            isLoading = false

                            val error = when {
                                error?.description?.contains("ERR_CONNECTION_REFUSED") == true -> WebViewError.CONNECTION_REFUSED
                                error?.description?.contains("ERR_INTERNET_DISCONNECTED") == true -> WebViewError.CONNECTION_DISCONNECTED
                                else -> WebViewError.UNKNOWN
                            }
                            updateWebViewError(error)
                        }
                    }
                    webViewInstance = this
                }
            },
            update = { webView ->
                isLoading = true
                didTimeout = false
                if (ConnectivityUtils.isNetworkAvailable(context)) {
                    webView.loadUrl(url)
                    // fire and forget content update
                    updateContent()
                } else {
                    webView.loadDataWithBaseURL(
                        url,
                        webContent.orEmpty(),
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (didTimeout) {
            Text("Loading timed out. Please try again.")
        }
    }
}