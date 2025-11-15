package com.timeskip.ezlink.features.contentHtml

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.timeskip.ezlink.features.link.data.Link

@Entity(
    tableName = "content_html",
    foreignKeys = [ForeignKey(
        entity = Link::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("linkId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["linkId"])]
)
data class ContentHtml(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val linkId: Int,
    val content: String
)
