package com.timeskip.ezlink.features.link.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.timeskip.ezlink.features.tag.data.Tag
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(
    tableName = "link",
    foreignKeys = [ForeignKey(
        entity = Tag::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("tagName"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["tagName"])]
)
@Serializable
@Parcelize
data class Link(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val tagName: String,
    val url: String,
    val iconUrl: String?,
    val title: String,
    val description: String
) : Parcelable