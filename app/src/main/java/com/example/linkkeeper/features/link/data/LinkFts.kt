package com.example.linkkeeper.features.link.data

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "link_fts")
@Fts4(contentEntity = Link::class)
data class LinkFts(val title: String, val description: String)