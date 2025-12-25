package com.timeskip.ezlink.features.link.search

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.views.MyTextField
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.list.LinkItem

@Composable
internal fun LinkSearchScreen(
    search: String,
    onBack: () -> Unit,
    onItemClick: (Link, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<LinkSearchViewModel>()
    val search by viewModel.searchLiveData.observeAsState(search)
    val listLink by viewModel.linkListLiveData.observeAsState(emptyList())
    var currentSelectedLink by remember { mutableStateOf<Link?>(null) }
    val deleteLinkResult by viewModel.deleteLinkLiveData.observeAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painterResource(R.drawable.arrow_left),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBack() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyTextField(
            value = search,
            placeholder = "Search links...",
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onChange = viewModel::updateSearch,
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .focusRequester(focusRequester)
        )
        Spacer(modifier = Modifier.height(24.dp))
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(count = 2),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (listLink.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    val emptyMessage = "No links found"
                    Text(
                        emptyMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(listLink.size) {
                LinkItem(
                    listLink[it],
                    onNavigateToEditor = { link -> onItemClick(link, true /* isEdit */) },
                    onLongClick = { link -> currentSelectedLink = link }
                )
            }
        }
    }

    if (currentSelectedLink != null) {
        AlertDialog(
            onDismissRequest = { currentSelectedLink = null },
            title = { Text("Delete Link") },
            text = { Text("Are you sure you want to delete this link?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteLink(currentSelectedLink ?: return@TextButton) }
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
