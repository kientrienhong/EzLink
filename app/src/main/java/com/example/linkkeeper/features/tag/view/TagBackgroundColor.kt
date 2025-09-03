package com.example.linkkeeper.features.tag.view

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import com.example.linkkeeper.features.tag.data.Tag

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

private enum class TagBackgroundColor(val nameTag: String, @ColorInt val color: Long) {
    FAVORITE("Favorite", 0xFFFAE8E7),
    READ_LATER("Read later", 0xFFE8F0FA),
    PERSONAL("Personal", 0xFFFAE8FB),
    USER_TAG("", 0xFFFFFFFF);

    companion object {
        fun fromNameTag(nameTag: String): TagBackgroundColor {
            return entries.find { it.nameTag == nameTag } ?: USER_TAG
        }
    }
}