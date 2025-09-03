package com.example.linkkeeper.features.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.link.data.LinkDao
import com.example.linkkeeper.features.link.data.LinkFts
import com.example.linkkeeper.features.tag.data.Tag
import com.example.linkkeeper.features.tag.data.TagDao

@Database(entities = [Tag::class, Link::class, LinkFts::class], version = 1)
abstract class MyDb : RoomDatabase() {
    abstract fun getTagDao(): TagDao

    abstract fun getLinkDao(): LinkDao
}