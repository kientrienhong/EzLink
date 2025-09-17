package com.example.linkkeeper.features.link.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.linkkeeper.features.tag.data.Tag
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(
    tableName = "link",
    foreignKeys = [ForeignKey(
        entity = Tag::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("tagId"),
        onDelete = ForeignKey.CASCADE
    )]
)
@Serializable
@Parcelize
data class Link(
    val id: String,
    val tagId: Int,
    val url: String,
    val iconUrl: String?,
    val title: String,
    val description: String
) : Parcelable