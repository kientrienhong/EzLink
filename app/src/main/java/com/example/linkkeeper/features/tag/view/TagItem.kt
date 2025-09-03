package com.example.linkkeeper.features.tag.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.R
import com.example.linkkeeper.features.tag.data.Tag

@Composable
fun TagItem(
    tagViewItem: TagViewItem,
    onTagClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(tagViewItem.backgroundColor)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onTagClick(tagViewItem.tag.id ?: 0, tagViewItem.tag.name) },
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
        Image(
            painterResource(R.drawable.arrow_right),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(32.dp)
                .padding(end = 8.dp)
        )
    }
}

@Preview
@Composable
fun TagItemPreview() {
    TagItem(
        tagViewItem = TagViewItem(
            tag = Tag(id = 1, name = "Sample Tag", amountOfLink = 5),
            backgroundColor = Color(0xFFBB86FC)
        ),
        onTagClick = { _, _ -> },
        modifier = Modifier.padding(16.dp)
    )
}
