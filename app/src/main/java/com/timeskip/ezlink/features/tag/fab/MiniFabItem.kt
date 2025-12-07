package com.timeskip.ezlink.features.tag.fab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MiniFabItem(fabViewItem: FabViewItem) {
    Row(
        modifier = Modifier
            .wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fabViewItem.label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 17.64.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .clip(RoundedCornerShape(size = 8.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 4.dp, horizontal = 12.dp)
        )
        FloatingActionButton(
            onClick = { fabViewItem.onClick() },
            modifier = Modifier.size(50.dp),
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(
                painter = painterResource(id = fabViewItem.iconRes),
                contentDescription = fabViewItem.label,
                modifier = Modifier
                    .size(24.dp),
                tint = Color.White
            )
        }
    }
}