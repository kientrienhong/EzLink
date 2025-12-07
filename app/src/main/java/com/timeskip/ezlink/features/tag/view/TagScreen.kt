package com.timeskip.ezlink.features.tag.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.timeskip.ezlink.features.tag.data.Tag

@Composable
internal fun TagScreen(
    modifier: Modifier,
    onTagClick: (String) -> Unit,
    onSearchClick: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<TagViewModel>()
    val stateFlowTagList by viewModel.tagListLiveData.observeAsState(emptyList())
    val stateFlowInitialLoad by viewModel.initialLoadLiveData.observeAsState(ApiResult.Loading())
    val stateTagCreating by viewModel.createTagLiveData.observeAsState()
    val stateDeleteTagResult by viewModel.deleteTagLiveData.observeAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentSelectedTag by remember { mutableStateOf<Tag?>(null) }

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

    TagScreenMainContent(
        modifier,
        stateFlowTagList,
        stateFlowInitialLoad,
        currentSelectedTag,
        currentSelectedTagChanged = { currentSelectedTag = it },
        onTagClick = onTagClick,
        onAddButton = { showBottomSheet = true },
        onDeleteTag = viewModel::deleteTag,
        onSearchClick = onSearchClick
    )
    if (showBottomSheet) {
        AddItemBottomSheet(
            title = "Add tag",
            stateCreate = stateTagCreating,
            onDismissRequest = { showBottomSheet = false },
            onSubmitWithEditTextValue = viewModel::createTag,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TagScreenMainContent(
    modifier: Modifier = Modifier,
    stateFlowTagList: List<TagViewItem>,
    stateFlowInitialLoad: ApiResult<Unit>,
    currentSelectedTag: Tag?,
    currentSelectedTagChanged: (Tag?) -> Unit,
    onTagClick: (String) -> Unit,
    onAddButton: () -> Unit,
    onDeleteTag: (Tag) -> Unit,
    onSearchClick: (String) -> Unit
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
            Image(
                painterResource(R.drawable.plus_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onAddButton() }
            )
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
            Modifier.fillMaxHeight()
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
        onAddButton = {},
        onDeleteTag = {},
        onSearchClick = {}
    )
}



