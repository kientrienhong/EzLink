package com.example.linkkeeper.features.tag.view

import androidx.annotation.DrawableRes
import com.example.linkkeeper.R
import com.example.linkkeeper.features.tag.data.Tag

object TagIconResourceProvider {
    @DrawableRes
    fun getIconResourceFromTag(tag: Tag): Int {
        if (!tag.isDefaultCreated) {
            return R.drawable.tag
        }
        val nameTag = tag.name
        return TagBackgroundColor.fromNameTag(nameTag).resource
    }
}

enum class TagBackgroundColor(val nameTag: String, @DrawableRes val resource: Int) {
    FAVORITE("Favorite", R.drawable.favorite),
    READ_LATER("Read later", R.drawable.tray),
    PERSONAL("Personal", R.drawable.person),
    USER_TAG("", R.drawable.tag);

    companion object {
        fun fromNameTag(nameTag: String): TagBackgroundColor {
            return entries.find { it.nameTag == nameTag } ?: USER_TAG
        }
    }
}