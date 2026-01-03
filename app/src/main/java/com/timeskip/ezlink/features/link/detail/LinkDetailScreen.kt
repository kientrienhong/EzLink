package com.timeskip.ezlink.features.link.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.ConnectivityObserver
import com.timeskip.ezlink.features.common.ImageStorageHelper
import com.timeskip.ezlink.features.common.views.WebViewWithTimeout
import com.timeskip.ezlink.features.contentHtml.ContentHtml
import com.timeskip.ezlink.features.link.data.Link

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LinkDetailScreen(
    link: Link,
    popNavigation: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<LinkDetailViewModel>()
    val updateResult by viewModel.linkUpdateResultLiveData.observeAsState()
    val crawlResult by viewModel.crawlWebResultLiveData.observeAsState()
    val contentHtml by viewModel.getContentHtmlLiveData(link.id ?: 0).observeAsState()
    val context = LocalContext.current
    var webViewError by remember { mutableStateOf<WebViewError?>(null) }
    val networkStatus by viewModel.networkStatusFlow.collectAsState(initial = ConnectivityObserver.Status.Available)
    var isAlertOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var isOpenDialog by remember { mutableStateOf(false) }
    val listTagName by viewModel.listTagName.observeAsState(emptyList())
    val localLink by viewModel.linkLiveData.observeAsState(link)
    val bottomPaddingValue = if (ImageStorageHelper.isLocalStoredImage(context, localLink.url)) {
        paddingValues.calculateBottomPadding()
    } else {
        paddingValues.calculateBottomPadding() + 56.dp + 24.dp
    }
    val layoutDirection = LocalLayoutDirection.current

    DisposableEffect(Unit) {
        onDispose {
            viewModel.reset()
        }
    }

    LaunchedEffect(link) {
        viewModel.initializeLink(link)
    }

    LaunchedEffect(updateResult) {
        when (val result = updateResult) {
            is ApiResult.Success -> {
                isOpenDialog = false
            }

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
            linkId = localLink.id ?: 0,
            url = localLink.url,
            crawlResult,
            contentHtml,
            paddingValues,
            Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.TopStart),
            webViewError,
            popNavigation,
            viewModel::crawlContentHtml,
            onContentHtmlDeleteClick = { isAlertOpen = true },
            setIsOpenDialog = { isOpenDialog = it }
        )
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                start = paddingValues.calculateLeftPadding(layoutDirection),
                end = paddingValues.calculateRightPadding(layoutDirection),
                bottom = bottomPaddingValue
            ),
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = localLink.title.takeIf { it.isNotEmpty() } ?: "(No title)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = localLink.description.takeIf { it.isNotEmpty() } ?: "(No description)",
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
                        .verticalScroll(scrollState)
                ) {
                    if (ImageStorageHelper.isLocalStoredImage(context, localLink.url)) {
                        // Display local stored image
                        GlideImage(
                            model = localLink.url,
                            contentDescription = "Shared image",
                            modifier = Modifier.align(Alignment.Center),
                            contentScale = ContentScale.FillBounds,
                            loading = placeholder(R.drawable.picture),
                            failure = placeholder(R.drawable.picture)
                        )
                    } else {
                        // Display web URL in WebView
                        WebViewWithTimeout(
                            localLink.url,
                            Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                            webViewError,
                            contentHtml?.content,
                            networkStatus = networkStatus,
                            updateWebViewError = { webViewError = it },
                            updateContent = {
                                viewModel.refreshContentHtml(
                                    localLink.id ?: 0,
                                    localLink.url
                                )
                            }
                        )
                    }
                }
            }
        }

        if (!ImageStorageHelper.isLocalStoredImage(context, localLink.url)) {
            Button(
                onClick = {
                    val browserIntent = Intent(Intent.ACTION_VIEW, localLink.url.toUri())
                    context.startActivity(browserIntent)
                },
                modifier = modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(layoutDirection),
                        end = paddingValues.calculateEndPadding(layoutDirection),
                        bottom = paddingValues.calculateBottomPadding() + 8.dp
                    )
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
        }
        if (isAlertOpen) {
            AlertDialog(
                onDismissRequest = { isAlertOpen = false },
                title = { Text("Delete cached content") },
                text = { Text("Are you sure you want to delete the cached content?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteContentHtml(localLink.id ?: 0)
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
        if (isOpenDialog) {
            EditLinkBottomSheet(
                link = localLink,
                listTagName = listTagName,
                result = updateResult,
                onDismissRequest = { isOpenDialog = false },
                onSubmit = viewModel::updateLink,
                modifier = Modifier.fillMaxWidth()
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
    crawlResult: ApiResult<Unit>?,
    contentHtml: ContentHtml?,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    webViewError: WebViewError? = null,
    popNavigation: () -> Unit,
    onDownloadResourceClick: (Int, String) -> Unit,
    onContentHtmlDeleteClick: () -> Unit = {},
    setIsOpenDialog: (Boolean) -> Unit = {}
) {
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
    val layoutDirection = LocalLayoutDirection.current
    Row(
        modifier
            .padding(
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection),
            )
            .fillMaxWidth(),
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
            val isWebUrl = Patterns.WEB_URL.matcher(url).matches()

            if (isWebUrl &&
                webViewError != WebViewError.CONNECTION_REFUSED
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
            Icon(
                painterResource(R.drawable.edit),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { setIsOpenDialog(true) },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun PreviewLinkEditorScreenHeader() {
    LinkEditorScreenHeader(
        1,
        "https://www.example.com",
        crawlResult = null,
        contentHtml = null,
        PaddingValues(0.dp),
        Modifier.padding(16.dp),
        popNavigation = {},
        onDownloadResourceClick = { _, _ -> }
    )
}


enum class WebViewError(val description: String) {
    CONNECTION_REFUSED("We don't support preview for this link."),
    CONNECTION_DISCONNECTED("Network disconnected. Please check your internet connection."),
    UNKNOWN("An unknown error occurred while loading the page.")
}