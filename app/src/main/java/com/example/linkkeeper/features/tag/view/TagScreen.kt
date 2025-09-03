package com.example.linkkeeper.features.tag.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.ApiResult

@Composable
fun TagScreen(modifier: Modifier, onTagClick: (Int, String) -> Unit) {
    val viewModel = hiltViewModel<TagViewModel>()
    val stateFlowTagList by viewModel.tagListLiveData.observeAsState(emptyList())
    val stateFlowInitialLoad by viewModel.initialLoadLiveData.observeAsState(ApiResult.Loading())
    val stateTagCreating by viewModel.createTagLiveData.observeAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    TagScreenMainContent(
        modifier,
        stateFlowTagList,
        stateFlowInitialLoad,
        onTagClick,
        onAddButton = { showBottomSheet = true }
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
    onTagClick: (Int, String) -> Unit,
    onAddButton: () -> Unit
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
        ListTagItem(stateFlowTagList, stateFlowInitialLoad, onTagClick)
    }
}



