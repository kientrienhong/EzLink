package com.timeskip.ezlink.features.tag.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.timeskip.ezlink.R
import com.timeskip.ezlink.ShareInfoModel
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.ImageStorageHelper
import com.timeskip.ezlink.features.common.views.GrayLogoWithTextView
import com.timeskip.ezlink.features.common.views.MyTextField
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.tag.data.Tag
import com.timeskip.ezlink.features.tag.fab.FabButtonState
import com.timeskip.ezlink.features.tag.fab.FabViewItem
import com.timeskip.ezlink.features.tag.fab.MultiFloatingActionButton
import com.timeskip.ezlink.features.tag.fab.rememberMultiFabState

@Composable
internal fun TagScreen(
    onTagClick: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    shareInfoModel: ShareInfoModel?,
    onConsumeSharedIntent: () -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<TagViewModel>()

    val stateFlowTagList by viewModel.tagListLiveData.observeAsState(emptyList())
    val stateFlowInitialLoad by viewModel.initialLoadLiveData.observeAsState(ApiResult.Loading())
    val stateTagCreating by viewModel.createTagLiveData.observeAsState()
    val stateLinkCreating by viewModel.createLinkLiveData.observeAsState()
    val stateDeleteTagResult by viewModel.deleteTagLiveData.observeAsState()
    val url by viewModel.urlLiveData.observeAsState(shareInfoModel?.sharedUrl)
    var multiFabState by rememberMultiFabState()
    var showTagAddBottomSheet by remember { mutableStateOf(false) }
    var showLinkAddBottomSheet by remember { mutableStateOf(false) }
    var showShareImageBottomSheet by remember { mutableStateOf(false) }
    var currentSelectedTag by remember { mutableStateOf<Tag?>(null) }
    var urlTextInputEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(shareInfoModel) {
        val sharedTagName = shareInfoModel?.sharedTagName
        val sharedUrl = shareInfoModel?.sharedUrl
        if (sharedTagName != null && sharedUrl != null) {
            viewModel.createLink(sharedUrl, sharedTagName)
            urlTextInputEnabled = false
        } else if (sharedUrl != null) {
            if (ImageStorageHelper.isImageUrl(sharedUrl)) {
                showShareImageBottomSheet = true
            } else {
                viewModel.updateUrl(sharedUrl)
                showLinkAddBottomSheet = true
                urlTextInputEnabled = false
            }
        } else {
            urlTextInputEnabled = true
        }
    }

    LaunchedEffect(stateDeleteTagResult) {
        val result = stateDeleteTagResult
        when (result) {
            is ApiResult.Success -> currentSelectedTag = null
            is ApiResult.Error -> Toast.makeText(
                context,
                result.exception.message,
                Toast.LENGTH_LONG
            ).show()

            is ApiResult.Loading,
            null -> Unit
        }
    }

    LaunchedEffect(stateLinkCreating) {
        when (stateLinkCreating) {
            is ApiResult.Success -> {
                showShareImageBottomSheet = false
                showLinkAddBottomSheet = false
                Toast.makeText(
                    context,
                    "Link added successfully",
                    Toast.LENGTH_SHORT
                ).show()
                onConsumeSharedIntent()
                viewModel.resetCreateLinkLiveData()
            }

            is ApiResult.Error -> {
                Toast.makeText(
                    context,
                    (stateLinkCreating as ApiResult.Error<Link>).exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }

            is ApiResult.Loading,
            null -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TagScreenMainContent(
            stateFlowTagList,
            stateFlowInitialLoad,
            currentSelectedTag,
            currentSelectedTagChanged = { currentSelectedTag = it },
            onTagClick = onTagClick,
            onDeleteTag = viewModel::deleteTag,
            onSearchClick = onSearchClick,
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        )
        if (showTagAddBottomSheet) {
            AddItemBottomSheetWithSingleInput(
                title = "Add tag",
                stateCreate = stateTagCreating,
                onDismissRequest = {
                    showTagAddBottomSheet = false
                    viewModel.updateUrl(null)
                    onConsumeSharedIntent()
                },
                onSubmitWithEditTextValue = viewModel::createTag,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (showShareImageBottomSheet) {
            ShareImageBottomSheet(
                imageUrl = shareInfoModel?.sharedUrl.orEmpty(),
                listTagName = stateFlowTagList.map { it.tag.name },
                result = stateLinkCreating,
                tagName = "",
                onDismissRequest = {
                    showShareImageBottomSheet = false
                    viewModel.updateUrl(null)
                    onConsumeSharedIntent()
                },
                onSubmit = { imageUri, tagName ->
                    viewModel.createLink(imageUri, tagName)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (showLinkAddBottomSheet) {
            AddLinkBottomSheet(
                listTagName = stateFlowTagList.map { it.tag.name },
                result = stateLinkCreating,
                tagName = "",
                url = url.orEmpty(),
                onDismissRequest = {
                    showLinkAddBottomSheet = false
                    viewModel.updateUrl(null)
                    onConsumeSharedIntent()
                },
                onSubmit = viewModel::createLink,
                modifier = Modifier.fillMaxWidth(),
                urlInputEnabled = urlTextInputEnabled
            )
        }
        if(multiFabState == FabButtonState.Expand) {
            // Dim background when FAB is expanded
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.01f))
                    .clickable {
                        multiFabState = multiFabState.toggleValue()
                    }
            )
        }

        Box(
            modifier = modifier.fillMaxSize()
        ) {
            MultiFloatingActionButton(
                listOf(
                    FabViewItem(
                        label = "Add Tag",
                        iconRes = R.drawable.tag,
                        onClick = { showTagAddBottomSheet = true }
                    ),
                    FabViewItem(
                        label = "Add Link",
                        iconRes = R.drawable.link,
                        onClick = { showLinkAddBottomSheet = true }
                    ),
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 24.dp),
                multiFabState
            ) {
                multiFabState = it
            }
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun TagScreenMainContent(
    stateFlowTagList: List<TagViewItem>,
    stateFlowInitialLoad: ApiResult<Unit>,
    currentSelectedTag: Tag?,
    currentSelectedTagChanged: (Tag?) -> Unit,
    onTagClick: (String) -> Unit,
    onDeleteTag: (Tag) -> Unit,
    onSearchClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val localLayoutDirection = LocalLayoutDirection.current
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            start = contentPadding.calculateLeftPadding(localLayoutDirection),
            end = contentPadding.calculateRightPadding(localLayoutDirection),
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        )
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        listTagItemWithHeader(
            stateFlowTagList = stateFlowTagList,
            stateFlowInitialLoad = stateFlowInitialLoad,
            onTagClick = onTagClick,
            onLongClick = currentSelectedTagChanged,
            onSearchClick = onSearchClick
        )
    }

    if (currentSelectedTag != null) {
        AlertDialog(
            onDismissRequest = { currentSelectedTagChanged(null) },
            title = { Text("Delete Tag") },
            text = { Text("Are you sure you want to delete this tag?") },
            confirmButton = {
                TextButton(onClick = { onDeleteTag(currentSelectedTag) }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { currentSelectedTagChanged(null) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun LazyListScope.listTagItemWithHeader(
    stateFlowTagList: List<TagViewItem>,
    stateFlowInitialLoad: ApiResult<Unit>,
    onTagClick: (String) -> Unit,
    onLongClick: (Tag) -> Unit,
    onSearchClick: (String) -> Unit,
) {
    when (stateFlowInitialLoad) {
        is ApiResult.Loading -> {
            item { Text("Loading") }
        }

        is ApiResult.Success -> {
            // Header: EzLink title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "EzLink", style = MaterialTheme.typography.headlineSmall)
                }
            }

            // Header: Hero image
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painterResource(R.drawable.link_home_illustration),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }

            // Header: Search field
            item {
                MyTextField(
                    value = "Search links...",
                    onChange = {},
                    leadingIcon = {
                        Icon(
                            painterResource(R.drawable.search),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    enabled = false,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .clickable { onSearchClick("") }
                )
            }

            // Header: "Your Tags" label
            item {
                Spacer(Modifier.height(16.dp))
                Text(text = "Your Tags", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }

            // Tag list items
            if (stateFlowTagList.isEmpty()) {
                item {
                    GrayLogoWithTextView(
                        modifier = Modifier.fillMaxSize(),
                        textContent = "No tags found. Click the '+' button to create a new tag."
                    )
                }
            }
            items(
                count = stateFlowTagList.size,
                key = { index -> stateFlowTagList[index].tag.name }
            ) { index ->
                val tagViewItem = stateFlowTagList[index]
                TagItem(tagViewItem, onTagClick, onLongClick)
            }
        }

        is ApiResult.Error -> {
            item {
                GrayLogoWithTextView(
                    modifier = Modifier.fillMaxSize(),
                    textContent = "There is unexpected error occurred. Please kill and re-open app again."
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTagScreenMainContent() {
    TagScreenMainContent(
        stateFlowTagList = listOf(
            TagViewItem(Tag("Sample Tag 1"), Color(0xFFDC9F4C), R.drawable.heart),
            TagViewItem(Tag("Sample Tag 2"), Color(0xFFF1B4FE), R.drawable.tag),
            TagViewItem(Tag("Sample Tag 3"), Color(0xFF628CCE), R.drawable.book)
        ),
        stateFlowInitialLoad = ApiResult.Success(Unit),
        currentSelectedTag = null,
        currentSelectedTagChanged = {},
        onTagClick = {},
        onDeleteTag = {},
        onSearchClick = {}
    )
}
