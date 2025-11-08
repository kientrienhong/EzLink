package com.timeskip.ezlink.features.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.timeskip.ezlink.features.contentHtml.ContentHtml
import com.timeskip.ezlink.features.contentHtml.ContentHtmlDao
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkDao
import com.timeskip.ezlink.features.link.data.LinkFts
import com.timeskip.ezlink.features.tag.data.Tag
import com.timeskip.ezlink.features.tag.data.TagDao

@Database(entities = [Tag::class, Link::class, LinkFts::class, ContentHtml::class], version = 1)
abstract class MyDb : RoomDatabase() {
    abstract fun getTagDao(): TagDao

    abstract fun getLinkDao(): LinkDao

    abstract fun getContentHtmlDao(): ContentHtmlDao
}