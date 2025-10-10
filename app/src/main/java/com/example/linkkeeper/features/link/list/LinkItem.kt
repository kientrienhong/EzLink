package com.example.linkkeeper.features.link.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.linkkeeper.R
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.ui.theme.LinkKeeperTheme
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun LinkItem(
    link: Link,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
    onOpenClick: () -> Unit = {}
) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val readableDate = dateFormat.format(Date(link.dateTimeCreated))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(customColors.surface)
            .border(BorderStroke(1.dp, customColors.border), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                GlideImage(
                    model = link.iconUrl,
                    contentDescription = link.title,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = link.title,
                    style = customTypography.headline,
                    color = customColors.primary,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Icon(
                painter = painterResource(R.drawable.edit),
                contentDescription = "Edit",
                tint = customColors.subtext,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onEditClick() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = customColors.border
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = link.description,
            style = customTypography.body,
            color = customColors.subtext,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = readableDate,
                style = customTypography.caption,
                color = customColors.subtext
            )
            Button(
                onClick = onOpenClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = customColors.background
                ),
                shape = RoundedCornerShape(40.dp)
            ) {
                Icon(
                    painterResource(R.drawable.arrow_up_right),
                    contentDescription = null,
                    Modifier
                        .width(18.dp)
                        .height(20.dp)
                        .padding(end = 5.dp),
                    tint = customColors.primary
                )
                Text(
                    text = "Open",
                    style = customTypography.subhead,
                    color = customColors.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun LinkItemPreview() {
    LinkKeeperTheme {
        LinkItem(
            link = Link(
                id = "1",
                title = "Example Link",
                url = "https://example.com/image.png",
                description = "This is a brief description of the example link. It provides an overview of what the link is about.",
                dateTimeCreated = System.currentTimeMillis(),
                tagId = "1",
                iconUrl = null
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}