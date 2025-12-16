package com.timeskip.ezlink.features.tag.view

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.timeskip.ezlink.features.tag.data.Tag

data class TagViewItem(
    val tag: Tag,
    val backgroundColor: Color,
    @DrawableRes val imageResource: Int
)