package com.example.linkkeeper.features.link.list

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.views.MyTextField
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.tag.view.AddItemBottomSheet

@Composable
fun LinkScreen(
    modifier: Modifier = Modifier,
    navigateToLinkEditor: (Link, Boolean) -> Unit,
    popBackStack: () -> Unit
) {
    val context: Context = LocalContext.current
    val viewModel = hiltViewModel<LinkScreenViewModel>()
    val tagRetrievingResult by viewModel.tagRetrievingResultLiveData.observeAsState()
    val urlValidationResult by viewModel.linkValidationLiveData.observeAsState()
    val listLink by viewModel.linkListLiveData.observeAsState()
    val searchValue by viewModel.searchLiveData.observeAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.getTagName() }

    LaunchedEffect(urlValidationResult) {
        val result = urlValidationResult
        when (result) {
            is ApiResult.Error -> {
                Toast.makeText(
                    context,
                    result.exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }

            is ApiResult.Success -> {
                navigateToLinkEditor(result.data, false /* isEdit */)
                showBottomSheet = false
                viewModel.resetLinkValidationLiveData()
            }

            is ApiResult.Loading,
            null -> Unit
        }
    }

    when (val tagResult = tagRetrievingResult) {
        is ApiResult.Success -> LinkScreenContent(
            viewModel.tagId,
            tagResult.data,
            urlValidationResult,
            listLink,
            searchValue.orEmpty(),
            modifier,
            showBottomSheet,
            viewModel::validateUrl,
            popBackStack,
            navigateToLinkEditor,
            viewModel::updateSearch,
            { showBottomSheet = it },
        )

        is ApiResult.Error -> Text("Error")
        is ApiResult.Loading,
        null ->
            // Shimmer loading
            Text("Loading")
    }

}

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun LinkScreenContent(
    tagId: Int,
    tagName: String?,
    urlValidationResult: ApiResult<Link>?,
    listLink: List<Link>?,
    searchValue: String,
    modifier: Modifier,
    showBottomSheet: Boolean,
    validateUrl: (Int, String) -> Unit,
    popBackStack: () -> Unit,
    onNavigateToEditor: (Link, Boolean) -> Unit,
    updateSearchValue: (String) -> Unit,
    updateShowBottomSheet: (Boolean) -> Unit
) {
    Column(modifier.padding(horizontal = 16.dp)) {
        LinkScreenHeader(
            tagName = tagName,
            popBackStack = popBackStack
        ) { updateShowBottomSheet(it) }
        MyTextField(searchValue, updateSearchValue, Modifier.padding(bottom = 8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 2),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (listLink?.isEmpty() == true) {
                item {
                    val emptyMessage = if (searchValue.isNotEmpty()) {
                        "No links found for '$searchValue'. Click the '+' button to create a new link."
                    } else {
                        "No links found"
                    }
                    Text(
                        emptyMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(listLink?.size ?: 0) {
                LinkItem(
                    listLink.orEmpty()[it],
                    onNavigateToEditor = { link ->
                        onNavigateToEditor(link, true /* isEdit */)
                    },
                    onDeleteClick = {}
                )
            }
        }
    }

    if (showBottomSheet) {
        AddItemBottomSheet(
            title = "Add url link",
            stateCreate = urlValidationResult,
            onDismissRequest = { updateShowBottomSheet(false) },
            onSubmitWithEditTextValue = { validateUrl(tagId, it) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LinkScreenHeader(
    modifier: Modifier = Modifier,
    tagName: String?,
    popBackStack: () -> Unit,
    setShowBottomSheet: (Boolean) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painterResource(R.drawable.arrow_left),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { popBackStack() }
        )
        Text(
            tagName.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Image(
            painterResource(R.drawable.plus_icon),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { setShowBottomSheet(true) }
        )
    }
}