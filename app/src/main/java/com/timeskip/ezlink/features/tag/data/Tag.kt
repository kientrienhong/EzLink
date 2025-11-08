package com.timeskip.ezlink.features.tag.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val amountOfLink: Int = 0,
    val isRead: Boolean = false,
    val isDefaultCreated: Boolean = false
)