package com.timeskip.ezlink.features.tag.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.tag.data.Tag

@Composable
fun TagScreen(modifier: Modifier, onTagClick: (Int, String) -> Unit) {
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
        onDeleteTag = viewModel::deleteTag
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
    onTagClick: (Int, String) -> Unit,
    onAddButton: () -> Unit,
    onDeleteTag: (Tag) -> Unit
) {
    Column(modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Tag",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Image(
                painterResource(R.drawable.plus_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onAddButton() }
            )
        }
        ListTagItem(stateFlowTagList, stateFlowInitialLoad, onTagClick, currentSelectedTagChanged)
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



