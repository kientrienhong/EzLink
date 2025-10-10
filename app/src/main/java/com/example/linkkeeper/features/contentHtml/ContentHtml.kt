package com.example.linkkeeper.features.contentHtml

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.linkkeeper.features.link.data.Link

@Entity(
    tableName = "content_html",
    foreignKeys = [ForeignKey(
        entity = Link::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("linkId"),
        onDelete = ForeignKey.Companion.CASCADE
    )]
)
data class ContentHtml(
    @PrimaryKey
    val id: String,
    val linkId: String,
    val content: String? = null,
    // Indicates whether the content HTML is supported for this link
    // Because some websites may have restrictions that cannot crawl data
    val isSupported: Boolean
)