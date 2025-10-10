package com.example.linkkeeper.features.tag.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.R
import com.example.linkkeeper.features.tag.data.Tag
import com.example.linkkeeper.ui.theme.LocalCustomColors

@Composable
fun TagItem(
    tagViewItem: TagViewItem,
    onTagClick: (String, String) -> Unit,
    onLongClick: (Tag) -> Unit,
    modifier: Modifier = Modifier,
) {
    val customColors = LocalCustomColors.current
    Row(
        modifier
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        val tagName = tagViewItem.tag.name
                        if (TagBackgroundColor.fromNameTag(tagName) == TagBackgroundColor.USER_TAG) {
                            onLongClick(tagViewItem.tag)
                        }
                    },
                    onTap = { onTagClick(tagViewItem.tag.id, tagViewItem.tag.name) }
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.padding(start = 8.dp)) {
            Text(
                tagViewItem.tag.name,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                tagViewItem.tag.amountOfLink.toString(),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Icon(
            painterResource(R.drawable.arrow_right),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(32.dp)
                .padding(end = 8.dp),
            tint = customColors.textPlaceholder
        )
    }
}
