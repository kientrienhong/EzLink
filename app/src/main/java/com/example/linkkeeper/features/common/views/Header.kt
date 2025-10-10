package com.example.linkkeeper.features.common.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography

@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    leftIcon: @Composable (() -> Unit)? = null,
    rightIcon: @Composable (() -> Unit)? = null
) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leftIcon?.invoke()
        Text(
            text = title,
            style = customTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = customColors.text
        )
        rightIcon?.invoke()
    }
}