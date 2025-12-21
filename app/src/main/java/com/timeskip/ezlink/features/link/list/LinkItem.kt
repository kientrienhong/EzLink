package com.timeskip.ezlink.features.link.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.link.data.Link

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun LinkItem(
    link: Link,
    modifier: Modifier = Modifier,
    onNavigateToEditor: (Link) -> Unit,
    onLongClick: (Link) -> Unit
) {
    val title = link.title.ifEmpty {
        "(No title)"
    }

    val description = link.description.ifEmpty {
        "(No description)"
    }

    Column(
        modifier = modifier
            .shadow(8.dp, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick(link) },
                    onTap = { onNavigateToEditor(link) }
                )
            }
            .padding(20.dp)
    ) {
        if (link.iconUrl != null) {
            GlideImage(
                model = link.iconUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp)),
                loading = placeholder(R.drawable.picture),
                failure = placeholder(R.drawable.picture)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = link.url,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewLinkItemLightMode() {
    LinkItem(
        Link(
            1,
            "",
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
            2,
            "",
            url = "https://abc.com",
            iconUrl = "https://logo.clearbit.com/medium.com",
            title = "Abd.test",
            description = "asdasdsaddsaaoverflowoverflowoverflow\n\noverflowoverflowoverflowoverflowdasdasdasdasdasdddddddsdasdasddas"
        ),
        onNavigateToEditor = {}
    ) {}
}