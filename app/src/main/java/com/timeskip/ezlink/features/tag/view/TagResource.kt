package com.timeskip.ezlink.features.tag.view

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.timeskip.ezlink.R

enum class TagResource(
    val nameTag: String,
    @ColorInt val color: Long,
    @DrawableRes val iconRes: Int
) {
    FAVORITE("Favorite", 0xFFC26CE6, R.drawable.heart),
    READ_LATER("Read later", 0xFFDC9F4C, R.drawable.book),
    PERSONAL("Personal", 0xFF628CCE, R.drawable.profile),
    USER_TAG("", 0xFFFE815A, R.drawable.tag);

    companion object {
        fun fromNameTag(nameTag: String): TagResource {
            return entries.find { it.nameTag == nameTag } ?: USER_TAG
        }
    }
}