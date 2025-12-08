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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.ConnectivityObserver
import com.timeskip.ezlink.features.common.views.WebViewWithTimeout
import com.timeskip.ezlink.features.contentHtml.ContentHtml
import com.timeskip.ezlink.features.link.data.Link

@Composable
fun LinkDetailScreen(
    link: Link,
    popNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var description by remember(link) { mutableStateOf(link.description) }
    var title by remember(link) { mutableStateOf(link.title) }
    val viewModel = hiltViewModel<LinkDetailViewModel>()
    val updateResult by viewModel.linkUpdateResultLiveData.observeAsState()
    val crawlResult by viewModel.crawlWebResultLiveData.observeAsState()
    val contentHtml by viewModel.getContentHtmlLiveData(link.id ?: 0).observeAsState()
    val context = LocalContext.current
    var webViewError by remember { mutableStateOf<WebViewError?>(null) }
    val networkStatus by viewModel.networkStatusFlow.collectAsState(initial = ConnectivityObserver.Status.Available)
    var isAlertOpen by remember { mutableStateOf(false) }
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

    Box(
        modifier
            .padding(horizontal = 16.dp)
            .background(Color.Transparent)
    ) {
        LinkEditorScreenHeader(
            linkId = link.id ?: 0,
            url = link.url,
            updateResult,
            crawlResult,
            contentHtml,
            Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.TopStart),
            webViewError,
            popNavigation,
            {
                val link = link.copy(title = title, description = description)
                viewModel.updateLink(link)
            },
            viewModel::crawlContentHtml,
            { isAlertOpen = true }
        )
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = link.title.takeIf { it.isNotEmpty() } ?: "(No title)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = link.description.takeIf { it.isNotEmpty() } ?: "(No description)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(9 / 16f)
                        .shadow(8.dp, MaterialTheme.shapes.large)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    WebViewWithTimeout(
                        link.url,
                        Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        webViewError,
                        contentHtml?.content,
                        networkStatus = networkStatus,
                        updateWebViewError = { webViewError = it },
                        updateContent = { viewModel.refreshContentHtml(link.id ?: 0, link.url) }
                    )
                }
            }
        }

        Button(
            onClick = {
                val browserIntent = Intent(Intent.ACTION_VIEW, link.url.toUri())
                context.startActivity(browserIntent)
            },
            modifier = modifier
                .padding(bottom = 24.dp)
                .height(56.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = "Open website",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
        }
        if (isAlertOpen) {
            AlertDialog(
                onDismissRequest = { isAlertOpen = false },
                title = { Text("Delete cached content") },
                text = { Text("Are you sure you want to delete the cached content?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteContentHtml(link.id ?: 0)
                            isAlertOpen = false
                            viewModel.resetCrawlWebResult()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isAlertOpen = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
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
    onDownloadResourceClick: (Int, String) -> Unit,
    onContentHtmlDeleteClick: () -> Unit = {}
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
                    downloadContentResource = { onDownloadResourceClick(linkId, url) },
                    onDelete = { onContentHtmlDeleteClick() },
                    Modifier.padding(end = 8.dp)
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
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        if (contentHtml != null) {
            if (contentHtml.content.isEmpty()) {
                Icon(
                    painterResource(R.drawable.download),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            downloadContentResource()
                        },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Icon(
                    painterResource(R.drawable.delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDelete() }
                )
            }
        } else {
            when (crawlResult) {
                null -> Icon(
                    painterResource(R.drawable.download),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            downloadContentResource()
                        },
                    tint = MaterialTheme.colorScheme.primary
                )

                is ApiResult.Success -> Icon(
                    painterResource(R.drawable.delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDelete() }
                )

                is ApiResult.Error -> Text("This link does not support preview")
                is ApiResult.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
    }
}


enum class WebViewError(val description: String) {
    CONNECTION_REFUSED("We don't support preview for this link."),
    CONNECTION_DISCONNECTED("Network disconnected. Please check your internet connection."),
    UNKNOWN("An unknown error occurred while loading the page.")
}