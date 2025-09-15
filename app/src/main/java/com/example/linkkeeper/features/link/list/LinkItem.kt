package com.example.linkkeeper.features.link.list

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onLongClick: (Link) -> Unit
) {
    Card(
        modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick(link) },
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewLinkItemLightMode() {
    LinkItem(
        Link(
            "1",
            1,
            url = "https://abc.com",
            iconUrl = "https://logo.clearbit.com/medium.com",
            title = "Abd.test",
            description = "asdasdsaddsaaoverflowoverflowoverflow\n\noverflowoverflowoverflowoverflowdasdasdasdasdasdddddddsdasdasddas"
        ),
        onNavigateToEditor = {}
    ) {}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewLinkItemDarkMode() {
    LinkItem(
        Link(
            "1",
            1,
            url = "https://abc.com",
            iconUrl = "https://logo.clearbit.com/medium.com",
            title = "Abd.test",
            description = "asdasdsaddsaaoverflowoverflowoverflow\n\noverflowoverflowoverflowoverflowdasdasdasdasdasdddddddsdasdasddas"
        ),
        onNavigateToEditor = {}
    ) {}
}