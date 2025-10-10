package com.example.linkkeeper.features.link.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.features.common.linkdialog.LinkModificationDialog
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.tag.view.TagViewItem

@Composable
fun ListLinkItem(
    links: List<Link>,
    allTagList: List<TagViewItem>,
    onNavigate: (Link) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddLinkDialog by remember { mutableStateOf(false) }
    var selectedLink by remember { mutableStateOf<Link?>(null) }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(links) { link ->
            LinkItem(
                link = link,
                onEditClick = {
                    selectedLink = link
                    showAddLinkDialog = true
                },
                onOpenClick = { onNavigate(link) }
            )
        }
    }

    if (showAddLinkDialog) {
        LinkModificationDialog(
            listAllTags = allTagList,
            link = selectedLink,
            onDismiss = {
                showAddLinkDialog = false
                selectedLink = null
            }
        )
    }
}
