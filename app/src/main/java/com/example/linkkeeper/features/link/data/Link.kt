package com.example.linkkeeper.features.link.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val tagId: Int,
    val url: String,
    val iconUrl: String?,
    val title: String,
    val description: String,
    val contentHtml: String
) : Parcelable