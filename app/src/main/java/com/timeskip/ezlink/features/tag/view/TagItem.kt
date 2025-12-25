package com.timeskip.ezlink.features.tag.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.tag.data.Tag

@Composable
internal fun TagItem(
    tagViewItem: TagViewItem,
    onTagClick: (String) -> Unit,
    onLongClick: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(4.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        val tagName = tagViewItem.tag.name
                        if (TagResource.fromNameTag(tagName) == TagResource.USER_TAG) {
                            onLongClick(tagViewItem.tag)
                        }
                    },
                    onTap = { onTagClick(tagViewItem.tag.name) }
                )
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tagViewItem.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(tagViewItem.imageResource),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = tagViewItem.tag.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(name = "TagItemPreview", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun TagItemPreview() {
    TagItem(
        tagViewItem = TagViewItem(
            tag = Tag(name = "Sample Tag", amountOfLink = 5),
            backgroundColor = Color(0xFFBB86FC),
            imageResource = R.drawable.heart
        ),
        onTagClick = { _ -> },
        onLongClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
