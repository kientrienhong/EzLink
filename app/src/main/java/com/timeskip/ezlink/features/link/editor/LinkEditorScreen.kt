package com.timeskip.ezlink.features.link.editor

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.views.TransparentTextField
import com.timeskip.ezlink.features.common.views.WebViewWithTimeout
import com.timeskip.ezlink.features.contentHtml.ContentHtml
import com.timeskip.ezlink.features.link.data.Link

@Composable
fun LinkEditorScreen(
    link: Link,
    popNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var description by remember(link) { mutableStateOf(link.description) }
    var title by remember(link) { mutableStateOf(link.title) }
    val viewModel = hiltViewModel<LinkEditorViewModel>()
    val updateResult by viewModel.linkUpdateResultLiveData.observeAsState()
    val crawlResult by viewModel.crawlWebResultLiveData.observeAsState()
    val contentHtml by viewModel.getContentHtmlLiveData(link.id ?: 0).observeAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var webViewError by remember { mutableStateOf<WebViewError?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.reset()
        }
    }

    LaunchedEffect(updateResult) {
        when (val result = updateResult) {
            is ApiResult.Success,
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
            .background(Color.Transparent)
    ) {
        LinkEditorScreenHeader(
            linkId = link.id ?: 0,
            url = link.url,
            updateResult,
            crawlResult,
            contentHtml,
            Modifier.padding(bottom = 8.dp),
            webViewError,
            popNavigation,
            {
                val link = link.copy(title = title, description = description)
                viewModel.updateLink(link)
            },
            viewModel::crawlContentHtml
        )
        TransparentTextField(
            value = title,
            placeholder = "Add a title",
            onValueChange = { title = it },
            modifier = Modifier.padding(bottom = 8.dp),
            singleLine = false,
            textStyle = MaterialTheme.typography.headlineSmall,
        )
        TransparentTextField(
            value = description,
            placeholder = "Add a description",
            onValueChange = { description = it },
            modifier = Modifier.padding(bottom = 8.dp),
            singleLine = false
        )
        WebViewWithTimeout(
            link.url,
            Modifier.weight(1f).fillMaxWidth(),
            webViewError,
            contentHtml?.content,
            updateWebViewError = { webViewError = it },
            updateContent = {
                viewModel.refreshContentHtml(link.id ?: 0, link.url)
            }
        )
    }
}

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun LinkEditorScreenHeader(
    linkId: Int,
    url: String,
    insertResult: ApiResult<Boolean>?,
    crawlResult: ApiResult<Unit>?,
    contentHtml: ContentHtml?,
    modifier: Modifier = Modifier,
    webViewError: WebViewError? = null,
    popNavigation: () -> Unit,
    onSaveClick: () -> Unit,
    onDownloadResourceClick: (Int, String) -> Unit
) {
    val context = LocalContext.current
    val windowSizeClass =
        calculateWindowSizeClass(activity = LocalContext.current as Activity)
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
            if (webViewError != WebViewError.CONNECTION_REFUSED
                && webViewError != WebViewError.CONNECTION_DISCONNECTED
            ) {
                DownloadIcon(
                    contentHtml,
                    crawlResult,
                    { onDownloadResourceClick(linkId, url) },
                    Modifier.padding(end = 8.dp)
                )
            }
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
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable { onSaveClick() }
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
        1,
        "https://www.example.com",
        insertResult = null,
        crawlResult = null,
        contentHtml = null,
        Modifier.padding(16.dp),
        popNavigation = {},
        onSaveClick = {},
        onDownloadResourceClick = { _, _ -> }
    )
}

@Composable
private fun DownloadIcon(
    contentHtml: ContentHtml?,
    crawlResult: ApiResult<Unit>?,
    downloadContentResource: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        if (contentHtml != null) {
            if (contentHtml.content.isEmpty()) {
                Text("This link does not support preview")
            } else {
                Icon(
                    painterResource(R.drawable.delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            when (crawlResult) {
                null -> Image(
                    painterResource(R.drawable.download),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            downloadContentResource()
                        }
                )

                is ApiResult.Success -> Icon(
                    painterResource(R.drawable.delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )

                is ApiResult.Error -> Text("This link does not support preview")
                is ApiResult.Loading -> CircularProgressIndicator()
            }
        }
    }
}


enum class WebViewError(val description: String) {
    CONNECTION_REFUSED("We don't support preview for this link."),
    CONNECTION_DISCONNECTED("Network disconnected. Please check your internet connection."),
    UNKNOWN("An unknown error occurred while loading the page.")
}