package com.example.linkkeeper.features.common.views

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MyWebView(
    url: String,
    modifier: Modifier = Modifier,
    onUpdate: (WebView) -> Unit = {},
    onFinish: () -> Unit = {},
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
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        view?.loadUrl(url)
                        return true
                    }
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.evaluateJavascript(
                            "(function() { return document.body ? document.body.innerHTML.length : 0; })();"
                        ) { result ->
                            val contentLength = result?.toIntOrNull() ?: 0
                            if(contentLength == 0) {
                                onError()
                            } else {
                                onFinish()
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.background(Color.Transparent),
        update = onUpdate
    )
}