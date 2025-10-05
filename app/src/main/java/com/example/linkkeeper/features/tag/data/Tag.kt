package com.example.linkkeeper.features.tag.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.linkkeeper.features.tag.view.TagIconResourceProvider
import com.example.linkkeeper.features.tag.view.TagViewItem

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey
    val id: String,
    val name: String,
    val amountOfLink: Int = 0,
    val isRead: Boolean = false,
    val isDefaultCreated: Boolean = false
) {
    fun toTagViewItem(): TagViewItem {
        val iconResource = TagIconResourceProvider.getIconResourceFromTag(this)
        return TagViewItem(this, iconResource)
    }
}