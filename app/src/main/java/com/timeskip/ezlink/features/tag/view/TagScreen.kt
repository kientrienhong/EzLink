package com.timeskip.ezlink.features.tag.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.views.MyTextField
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.tag.data.Tag
import com.timeskip.ezlink.features.tag.fab.FabViewItem
import com.timeskip.ezlink.features.tag.fab.MultiFloatingActionButton

@Composable
internal fun TagScreen(
    sharedTagName: String?,
    sharedUrl: String?,
    modifier: Modifier,
    onTagClick: (String) -> Unit,
    onSearchClick: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<TagViewModel>()
    val stateFlowTagList by viewModel.tagListLiveData.observeAsState(emptyList())
    val stateFlowInitialLoad by viewModel.initialLoadLiveData.observeAsState(ApiResult.Loading())
    val stateTagCreating by viewModel.createTagLiveData.observeAsState()
    val stateLinkCreating by viewModel.createLinkLiveData.observeAsState()
    val stateDeleteTagResult by viewModel.deleteTagLiveData.observeAsState()
    val url by viewModel.urlLiveData.observeAsState(sharedUrl)
    var showTagAddBottomSheet by remember { mutableStateOf(false) }
    var showLinkAddBottomSheet by remember { mutableStateOf(false) }
    var currentSelectedTag by remember { mutableStateOf<Tag?>(null) }

    LaunchedEffect(sharedTagName, sharedUrl) {
        if (sharedTagName != null && sharedUrl != null) {
            viewModel.createLink(sharedUrl, sharedTagName)
        } else if (sharedUrl != null) {
            viewModel.updateUrl(sharedUrl)
            showLinkAddBottomSheet = true
        } else if (sharedTagName != null) {
            // navigate to tag
            onTagClick(sharedTagName)
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
        Log.d("TagScreen", "stateLinkCreating: $stateLinkCreating")
        when (stateLinkCreating) {
            is ApiResult.Success -> {
                Toast.makeText(
                    context,
                    "Link added successfully",
                    Toast.LENGTH_LONG
                ).show()
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

    Box(modifier = modifier.fillMaxSize()) {
        TagScreenMainContent(
            stateFlowTagList,
            stateFlowInitialLoad,
            currentSelectedTag,
            currentSelectedTagChanged = { currentSelectedTag = it },
            onTagClick = onTagClick,
            onDeleteTag = viewModel::deleteTag,
            onSearchClick = onSearchClick,
            modifier = Modifier.fillMaxSize()
        )
        if (showTagAddBottomSheet) {
            AddItemBottomSheetWithSingleInput(
                title = "Add tag",
                stateCreate = stateTagCreating,
                onDismissRequest = {
                    showTagAddBottomSheet = false
                    viewModel.updateUrl(null)
                },
                onSubmitWithEditTextValue = viewModel::createTag,
                modifier = Modifier.fillMaxWidth(),
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
                },
                onSubmit = viewModel::createLink,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
                .padding(16.dp)
        )
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
) {
    Column(
        modifier
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "EzLink", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painterResource(R.drawable.link_home_illustration),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
        MyTextField(
            value = "",
            placeholder = "Search links...",
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
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
                .clickable { onSearchClick("") }
        )
        Spacer(Modifier.height(16.dp))
        Text(text = "Your Tags", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ListTagItem(
            stateFlowTagList,
            stateFlowInitialLoad,
            onTagClick,
            currentSelectedTagChanged,
            Modifier.weight(1f)
        )
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
}

@Preview
@Composable
private fun PreviewTagScreenMainContent() {
    TagScreenMainContent(
        stateFlowTagList = listOf(
            TagViewItem(Tag("Sample Tag 1"), Color(0xFFDC9F4C)),
            TagViewItem(Tag("Sample Tag 2"), Color(0xFFF1B4FE)),
            TagViewItem(Tag("Sample Tag 3"), Color(0xFF628CCE))
        ),
        stateFlowInitialLoad = ApiResult.Success(Unit),
        currentSelectedTag = null,
        currentSelectedTagChanged = {},
        onTagClick = {},
        onDeleteTag = {},
        onSearchClick = {}
    )
}



