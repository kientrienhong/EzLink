package com.example.linkkeeper.features.tag.view

import androidx.annotation.DrawableRes
import com.example.linkkeeper.features.tag.data.Tag

data class TagViewItem(val tag: Tag, @DrawableRes val iconResource: Int)