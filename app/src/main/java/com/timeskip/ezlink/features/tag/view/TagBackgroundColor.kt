package com.timeskip.ezlink.features.tag.view

import androidx.annotation.ColorInt
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.timeskip.ezlink.features.tag.data.Tag

object TagBackgroundColorProvider {
    @ColorInt
    fun getColorFromTag(tag: Tag): Color {
        if (!tag.isDefaultCreated) {
            return Color(TagBackgroundColor.USER_TAG.color)
        }
        val nameTag = tag.name
        val tagBackgroundColor = TagBackgroundColor.fromNameTag(nameTag).color
        return Color(tagBackgroundColor)
    }
}

enum class TagBackgroundColor(val nameTag: String, @ColorInt val color: Long) {
    FAVORITE("Favorite", 0xFFDC9F4C),
    READ_LATER("Read later", 0xFFF1B4FE),
    PERSONAL("Personal", 0xFF628CCE),
    USER_TAG("", 0xFFFE815A);

    companion object {
        fun fromNameTag(nameTag: String): TagBackgroundColor {
            return entries.find { it.nameTag == nameTag } ?: USER_TAG
        }
    }
}