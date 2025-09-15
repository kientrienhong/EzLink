package com.example.linkkeeper.features.link.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "content_html",
    foreignKeys = [ForeignKey(
        entity = Link::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("linkId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ContentHtml(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val linkId: Int,
    val content: String
)
