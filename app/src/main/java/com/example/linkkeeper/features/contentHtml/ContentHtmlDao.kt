package com.example.linkkeeper.features.contentHtml

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
abstract class ContentHtmlDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    abstract suspend fun insertContentHtml(contentHtml: ContentHtml): Long

    @Update
    abstract suspend fun updateContentHtml(contentHtml: ContentHtml): Int

    @Delete
    abstract suspend fun deleteContentHtml(contentHtml: ContentHtml): Int

    @Query("SELECT * FROM content_html WHERE linkId = :linkId")
    abstract fun getContentHtmlLiveData(linkId: String): LiveData<ContentHtml?>

    @Query("SELECT * FROM content_html WHERE linkId = :linkId")
    abstract suspend fun getContentHtml(linkId: String): ContentHtml?

    @Query("DELETE FROM content_html WHERE linkId = :linkId")
    abstract suspend fun deleteContentHtmlByLinkId(linkId: String): Int
}