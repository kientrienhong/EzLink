package com.example.linkkeeper.features.home.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.linkdialog.LinkModificationDialog
import com.example.linkkeeper.features.common.views.MyTextField
import com.example.linkkeeper.features.tag.data.Tag
import com.example.linkkeeper.features.tag.view.TagViewItem
import com.example.linkkeeper.ui.theme.LinkKeeperTheme
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onTagClick: (Tag) -> Unit = {}
) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current
    val viewModel = hiltViewModel<HomeViewModel>()
    val allTags by viewModel.allTagListLiveData.observeAsState()
    val (defaultTags, userTags) = allTags.orEmpty().partition { it.tag.isDefaultCreated }

    val createTagResult by viewModel.createTagLiveData.observeAsState()
    var searchText by remember { mutableStateOf("") }
    var showAddLinksDialog by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }

    LaunchedEffect(createTagResult) {
        when (createTagResult) {
            is ApiResult.Success -> showAddTagDialog = false
            is ApiResult.Error,
            is ApiResult.Loading,
            null -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(customColors.background)
            .padding(16.dp)
    ) {
        MyTextField(
            enable = false,
            value = searchText,
            onChange = { searchText = it },
            placeholder = {
                Text(
                    "Search",
                    style = customTypography.body,
                    color = customColors.textPlaceholder
                )
            },
            trailingIcon = {
                Image(
                    ImageVector.vectorResource(R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(44.dp)
                )
            },
            containerColor = customColors.surface,
            modifier = Modifier.clickable { onSearchClick() }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(Modifier.weight(1f)) {
            NameAndListTagItem(
                name = "DEFAULT",
                list = defaultTags,
                onTagClick = onTagClick,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            NameAndListTagItem(
                name = "OTHERS",
                list = userTags,
                onTagClick = onTagClick
            )
        }

        BottomNavigationBar(
            onAddFolderClick = { showAddTagDialog = true },
            onAddLinksClick = { showAddLinksDialog = true }
        )
    }

    if (showAddLinksDialog) {
        LinkModificationDialog(
            allTags.orEmpty(),
            onDismiss = { showAddLinksDialog = false }
        )
    }

    if (showAddTagDialog) {
        AddTagDialog(
            createTagResult,
            onDismiss = { showAddTagDialog = false },
            onCreate = viewModel::createTag
        )
    }
}

@Composable
private fun NameAndListTagItem(
    name: String,
    list: List<TagViewItem>,
    onTagClick: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current
    Text(
        name,
        style = customTypography.overline,
        color = customColors.subtext,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    LazyColumn(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(customColors.surface)
    ) {
        itemsIndexed(
            list,
            { index: Int, tagViewItem: TagViewItem -> tagViewItem.tag.id },
            contentType = { _: Int, _: TagViewItem -> "TagListItem" },
            itemContent = { index: Int, tagViewItem: TagViewItem ->
                TagListItem(
                    tagViewItem = tagViewItem,
                    onClick = { onTagClick(tagViewItem.tag) }
                )
                if (index != list.lastIndex) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 52.dp)
                            .height(1.dp)
                            .background(customColors.border)
                    )
                }
            },
        )
    }
}

@Composable
private fun TagListItem(
    tagViewItem: TagViewItem,
    onClick: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(tagViewItem.iconResource),
                contentDescription = tagViewItem.tag.name,
                modifier = Modifier.size(24.dp),
                tint = customColors.primary
            )
            Text(
                text = tagViewItem.tag.name,
                style = customTypography.body,
                color = customColors.text
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = tagViewItem.tag.amountOfLink.toString(),
                style = customTypography.body,
                color = customColors.subtext
            )
            Image(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = "Navigate",
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onAddFolderClick: () -> Unit,
    onAddLinksClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.folder_add),
            contentDescription = "Add Folder",
            modifier = Modifier
                .size(50.dp)
                .clickable { onAddFolderClick() }
        )

        Image(
            painter = painterResource(R.drawable.link_add),
            contentDescription = "Add Links",
            modifier = Modifier
                .size(50.dp)
                .clickable { onAddLinksClick() }
        )
    }
}

// Add preview for HomeScreen
@Preview
@Composable
fun HomeScreenPreview() {
    LinkKeeperTheme {
        HomeScreen(
            onSearchClick = { /* Handle search click */ },
            onTagClick = { /* Handle tag click */ }
        )
    }
}
