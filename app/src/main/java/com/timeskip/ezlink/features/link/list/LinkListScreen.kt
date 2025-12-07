package com.timeskip.ezlink.features.link.list

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.tag.view.AddItemBottomSheetWithSingleInput

@Composable
internal fun LinkScreen(
    modifier: Modifier = Modifier,
    navigateToLinkEditor: (Link, Boolean) -> Unit,
    popBackStack: () -> Unit
) {
    val context: Context = LocalContext.current
    val viewModel = hiltViewModel<LinkScreenViewModel>()
    val tagRetrievingResult by viewModel.tagRetrievingResultLiveData.observeAsState()
    val createLinkResult by viewModel.createLinkLiveData.observeAsState()
    val listLink by viewModel.linkListLiveData.observeAsState()
    val searchValue by viewModel.searchLiveData.observeAsState()
    val deleteLinkResult by viewModel.deleteLinkLiveData.observeAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentSelectedLink by remember { mutableStateOf<Link?>(null) }

    LaunchedEffect(Unit) { viewModel.getTagName() }

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

    when (val tagResult = tagRetrievingResult) {
        is ApiResult.Success -> Column(modifier.padding(horizontal = 16.dp)) {
            LinkScreenContent(
                tagResult.data,
                listLink,
                searchValue.orEmpty(),
                popBackStack,
                navigateToLinkEditor,
                viewModel::updateSearch,
                { showBottomSheet = it },
                { currentSelectedLink = it }
            )
            if (showBottomSheet) {
                AddItemBottomSheetWithSingleInput(
                    title = "Add url link",
                    stateCreate = createLinkResult,
                    onDismissRequest = { showBottomSheet = false },
                    onSubmitWithEditTextValue = { viewModel.validateUrlThenCreatingLink(it) },
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
                                viewModel.deleteLink(currentSelectedLink ?: return@TextButton)
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
        }

        is ApiResult.Error -> Text("Error")
        is ApiResult.Loading,
        null ->
            // Shimmer loading
            Text("Loading")
    }
}
