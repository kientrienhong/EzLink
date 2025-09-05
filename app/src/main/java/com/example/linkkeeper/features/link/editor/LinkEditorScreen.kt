package com.example.linkkeeper.features.link.editor

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.views.MyWebView
import com.example.linkkeeper.features.common.views.TransparentTextField
import com.example.linkkeeper.features.link.data.Link

@Composable
fun LinkEditorScreen(
    link: Link,
    isEdit: Boolean,
    popNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var description by remember { mutableStateOf(link.description) }
    var title by remember { mutableStateOf(link.title) }
    val viewModel = hiltViewModel<LinkEditorViewModel>()
    val insertResult by viewModel.resultMediatorLiveData.observeAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(insertResult) {
        when (val result = insertResult) {
            is ApiResult.Success -> viewModel.reset()
            is ApiResult.Loading,
            null -> Unit

            is ApiResult.Error ->
                Toast.makeText(context, result.exception.message, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        LinkEditorScreenHeader(
            Modifier.padding(bottom = 8.dp),
            link.url,
            insertResult,
            popNavigation,
        ) {
            val link = link.copy(title = title, description = description)
            if (isEdit) {
                viewModel.updateLink(link)
            } else {
                viewModel.insertLink(link)
            }
        }
        TransparentTextField(
            value = title,
            placeholder = "Add a title",
            onValueChange = { title = it },
            modifier = Modifier.padding(bottom = 8.dp),
            singleLine = false,
            textStyle = MaterialTheme.typography.headlineSmall
        )
        TransparentTextField(
            value = description,
            placeholder = "Add a description",
            onValueChange = { description = it },
            modifier = Modifier.padding(bottom = 8.dp),
            singleLine = false
        )
        PreviewWebContainer(link)
    }
}

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun LinkEditorScreenHeader(
    modifier: Modifier = Modifier,
    url: String,
    insertResult: ApiResult<Boolean>?,
    popNavigation: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val context = LocalContext.current
    val windowSizeClass =
        calculateWindowSizeClass(activity = LocalContext.current as android.app.Activity)
    val isBackArrowVisible = remember(windowSizeClass) {
        windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded
    }
    val horizontalArrangement = remember(windowSizeClass) {
        if (isBackArrowVisible) {
            Arrangement.SpaceBetween
        } else {
            Arrangement.End
        }
    }

    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        if (isBackArrowVisible) {
            Image(
                painterResource(R.drawable.arrow_left),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { popNavigation() }
            )
        }
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFBA8474))
                    .size(32.dp)
            ) {
                Image(
                    painterResource(R.drawable.browser),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                        .clickable {
                            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(browserIntent)
                        }
                )
            }
            when (insertResult) {
                is ApiResult.Loading -> CircularProgressIndicator()
                is ApiResult.Error,
                null -> Text(
                    "Save",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp).clickable { onSaveClick() }
                )

                is ApiResult.Success -> popNavigation()
            }
        }
    }
}

@Preview
@Composable
fun PreviewLinkEditorScreenHeader() {
    LinkEditorScreenHeader(
        Modifier.padding(16.dp),
        "https://www.example.com",
        null,
        {},
    ) {}
}

@Composable
private fun PreviewWebContainer(link: Link) {
    var webViewErrorType by remember { mutableStateOf(WebViewErrorType.None) }
    LaunchedEffect(webViewErrorType) {
        Log.d("LinkEditorScreen", "webViewErrorType $webViewErrorType")
    }

    when (webViewErrorType) {
        WebViewErrorType.None,
        WebViewErrorType.LocalHtmlLoadError -> MyWebView(
            Modifier
                .padding(top = 16.dp)
                .fillMaxHeight(),
            onUpdate = {
                when {
                    link.contentHtml.isNotEmpty() && webViewErrorType == WebViewErrorType.None ->
                        it.loadDataWithBaseURL(
                            null,
                            link.contentHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )

                    webViewErrorType == WebViewErrorType.LocalHtmlLoadError -> it.loadUrl(link.url)
                    webViewErrorType == WebViewErrorType.RemoteUrlLoadError -> Unit
                }
            },
            onError = {
                when (webViewErrorType) {
                    WebViewErrorType.None -> webViewErrorType =
                        WebViewErrorType.LocalHtmlLoadError

                    WebViewErrorType.LocalHtmlLoadError -> webViewErrorType =
                        WebViewErrorType.RemoteUrlLoadError

                    else -> Unit
                }
            }
        )

        WebViewErrorType.RemoteUrlLoadError -> Text("Does not support preview for this link")
    }
}

private enum class WebViewErrorType {
    None,
    LocalHtmlLoadError,
    RemoteUrlLoadError
}