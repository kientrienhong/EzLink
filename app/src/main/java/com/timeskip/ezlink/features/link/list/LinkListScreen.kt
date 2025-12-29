package com.timeskip.ezlink.features.link.list

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.tag.view.AddLinkBottomSheet

@Composable
internal fun LinkScreen(
    modifier: Modifier = Modifier,
    navigateToLinkEditor: (Link, Boolean) -> Unit,
    popBackStack: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val context: Context = LocalContext.current
    val viewModel = hiltViewModel<LinkScreenViewModel>()
    val createLinkResult by viewModel.createLinkLiveData.observeAsState()
    val listLink by viewModel.linkListLiveData.observeAsState()
    val searchValue by viewModel.searchLiveData.observeAsState("")
    val deleteLinkResult by viewModel.deleteLinkLiveData.observeAsState()
    val isLoadingMore by viewModel.isLoadingMore.observeAsState(false)
    val hasMoreItems by viewModel.hasMoreItems.observeAsState(true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentSelectedLink by remember { mutableStateOf<Link?>(null) }

    LaunchedEffect(createLinkResult) {
        val result = createLinkResult
        when (result) {
            is ApiResult.Error -> {
                Toast.makeText(
                    context,
                    result.exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }

            is ApiResult.Success -> {
                showBottomSheet = false
                viewModel.resetLinkValidationLiveData()
            }

            is ApiResult.Loading,
            null -> Unit
        }
    }

    LaunchedEffect(deleteLinkResult) {
        val result = deleteLinkResult
        when (result) {
            is ApiResult.Error -> {
                Toast.makeText(
                    context,
                    result.exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }

            is ApiResult.Success -> currentSelectedLink = null
            is ApiResult.Loading,
            null -> Unit
        }
    }
    Box(modifier.fillMaxSize()) {
        LinkScreenContent(
            viewModel.tagName,
            listLink,
            searchValue.orEmpty(),
            popBackStack,
            navigateToLinkEditor,
            viewModel::updateSearch,
            { currentSelectedLink = it },
            onLoadMore = {
                if (!isLoadingMore && hasMoreItems) {
                    viewModel.loadNextPage()
                }
            },
            modifier = Modifier.fillMaxSize(),
            isLoadingMore = isLoadingMore,
            paddingValues = paddingValues
        )
        if (showBottomSheet) {
            AddLinkBottomSheet(
                listOf(viewModel.tagName.orEmpty()),
                createLinkResult,
                tagName = viewModel.tagName.orEmpty(),
                url = "",
                onDismissRequest = { showBottomSheet = false },
                onSubmit = { url, tagName -> viewModel.validateUrlThenCreatingLink(url) },
                dropDownEnabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (currentSelectedLink != null) {
            AlertDialog(
                onDismissRequest = { currentSelectedLink = null },
                title = { Text("Delete Link") },
                text = { Text("Are you sure you want to delete this link?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteLink(context, currentSelectedLink ?: return@TextButton)
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { currentSelectedLink = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        Box(
            modifier = modifier.fillMaxSize().padding(paddingValues)
        ) {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp),
                    tint = Color.White
                )
            }
        }
    }
}