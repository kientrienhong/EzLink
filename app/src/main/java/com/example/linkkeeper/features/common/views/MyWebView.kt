package com.example.linkkeeper.features.common.views

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.util.Log
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MyWebView(
    modifier: Modifier = Modifier,
    onUpdate: (WebView) -> Unit = {},
    onError: () -> Unit = {}
) {
    AndroidView(
        factory = {
            WebView(it).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }.apply {
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {

                    override fun onRenderProcessGone(
                        view: WebView?,
                        detail: RenderProcessGoneDetail?
                    ): Boolean {
                        Log.e("WebView", "onRenderProcessGone")
                        return super.onRenderProcessGone(view, detail)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean = true

                    override fun onLoadResource(view: WebView?, url: String?) {
                        Log.e("WebView", "onLoadResource")
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        Log.e("WebView", "onPageFinished")
                        view?.evaluateJavascript(
                            "(function() { return document.body ? document.body.innerHTML.length : 0; })();"
                        ) { result ->
                            val contentLength = result?.toIntOrNull() ?: 0
                            if(contentLength == 0) {
                                onError()
                            }
                            Log.e("WebView", "contentLength $contentLength")
                        }
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        Log.e("WebView", "onPageStarted")
                    }

                    override fun onPageCommitVisible(view: WebView?, url: String?) {
                        Log.e("WebView", "onPageCommitVisible")
                    }

                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler?,
                        error: SslError?
                    ) {
                        onError()
                        Log.e("WebView", "WebView onReceivedSslError: $error")
                        super.onReceivedSslError(view, handler, error)
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        onError()
                        Log.e("WebView", "WebView onReceivedHttpError: ${errorResponse?.data}")
                        super.onReceivedHttpError(view, request, errorResponse)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        onError()
                        Log.e("WebView", "WebView onReceivedError: ${error?.description}")
                        Log.e("WebView", "WebView onReceivedError: ${error?.errorCode}")
                        super.onReceivedError(view, request, error)
                    }
                }
            }
        },
        modifier = modifier,
        update = onUpdate
    )
}