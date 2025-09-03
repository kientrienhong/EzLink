package com.example.linkkeeper.features.link.list

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.linkkeeper.features.link.data.Link


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LinkItem(
    link: Link,
    modifier: Modifier = Modifier,
    onNavigateToEditor: (Link) -> Unit,
    onDeleteClick: (Link) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showDialog = true },
                    onTap = { onNavigateToEditor(link) }
                )
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {

        if (link.iconUrl != null) {
            GlideImage(
                model = link.iconUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .aspectRatio(2.5f)
            )
        }
        Text(
            link.url,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            link.title,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (link.description.isNotEmpty()) {
            Text(
                link.description,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Link") },
            text = { Text("Are you sure you want to delete this link?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDeleteClick(link)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLinkItem() {
    LinkItem(
        Link(
            1,
            1,
            url = "https://abc.com",
            iconUrl = "https://logo.clearbit.com/medium.com",
            title = "Abd.test",
            description = "asdasdsaddsaaoverflowoverflowoverflow\n\noverflowoverflowoverflowoverflowdasdasdasdasdasdddddddsdasdasddas",
            contentHtml = "<html></html>"
        ),
        onNavigateToEditor = {}
    ) {}
}