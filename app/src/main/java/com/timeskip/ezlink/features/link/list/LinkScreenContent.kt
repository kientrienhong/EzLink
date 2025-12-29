package com.timeskip.ezlink.features.link.list

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.views.MyTextField
import com.timeskip.ezlink.features.link.data.Link
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun LinkScreenContent(
    tagName: String?,
    listLink: List<Link>?,
    searchValue: String,
    popBackStack: () -> Unit,
    onNavigateToEditor: (Link, Boolean) -> Unit,
    updateSearchValue: (String) -> Unit,
    onLongClickItem: (Link) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    isLoadingMore: Boolean = false
) {
    val items = listLink.orEmpty()
    val gridState = rememberLazyStaggeredGridState()

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { layoutInfo ->
                val totalItemsCount = layoutInfo.totalItemsCount
                val lastVisibleIndex =
                    layoutInfo.visibleItemsInfo.maxOfOrNull { it.index } ?: 0
                totalItemsCount to lastVisibleIndex
            }
            .distinctUntilChanged()
            .filter { (total, lastVisible) ->
                total > 0 && lastVisible >= total - 4
            }
            .collect {
                onLoadMore()
            }
    }


    Column(Modifier.fillMaxSize()) {
        LinkScreenHeader(
            tagName = tagName,
            popBackStack = popBackStack
        )
        MyTextField(
            value = searchValue,
            placeholder = "Search links...",
            onChange = updateSearchValue,
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        )

        LazyVerticalStaggeredGrid(
            state = gridState,
            columns = StaggeredGridCells.Fixed(count = 2),
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (items.isEmpty() && !isLoadingMore) {
                item(span = StaggeredGridItemSpan.FullLine) {
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

            items(items) { link ->
                LinkItem(
                    link,
                    onNavigateToEditor = { selected ->
                        onNavigateToEditor(selected, true /* isEdit */)
                    },
                    onLongClick = onLongClickItem
                )
            }

            // Bottom loading indicator
            if (isLoadingMore && items.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }


    }
}

@Preview
@Composable
private fun LinkScreenContentPreview() {
    Column {
        LinkScreenContent(
            tagName = "Sample Tag",
            listLink = List(10) {
                Link(
                    id = null,
                    url = "https://example.com/$it",
                    title = "Example Link Title $it",
                    description = "This is a description for example link number $it.",
                    iconUrl = null,
                    tagName = "Sample Tag"
                )
            },
            searchValue = "",
            popBackStack = {},
            onNavigateToEditor = { _, _ -> },
            updateSearchValue = {},
            onLongClickItem = {},
            onLoadMore = {},
            isLoadingMore = false
        )
    }
}

@Composable
private fun LinkScreenHeader(
    modifier: Modifier = Modifier,
    tagName: String?,
    popBackStack: () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
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
            text = tagName.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(Modifier.size(24.dp))
    }
}
