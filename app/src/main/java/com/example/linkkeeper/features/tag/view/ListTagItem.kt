package com.example.linkkeeper.features.tag.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.views.GrayLogoWithTextView

@Composable
@Stable
fun ListTagItem(
    stateFlowTagList: List<TagViewItem>,
    stateFlowInitialLoad: ApiResult<Unit>,
    onTagClick: (Int, String) -> Unit
) {
    when (stateFlowInitialLoad) {
        is ApiResult.Loading -> {
            // TODO Implement Shimmer
            Text("Loading")
        }

        is ApiResult.Success -> {
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
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
                    key = { index -> stateFlowTagList[index].tag.id ?: 0 }
                ) { index ->
                    val tagViewItem = stateFlowTagList[index]
                    TagItem(tagViewItem, onTagClick)
                }
            }
        }

        is ApiResult.Error -> GrayLogoWithTextView(
            modifier = Modifier.fillMaxSize(),
            textContent = "There is unexpected error occurred. Please kill and re-open app again."
        )
    }
}
