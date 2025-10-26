package com.example.linkkeeper.features.contentHtml

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
abstract class ContentHtmlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertContentHtml(contentHtml: ContentHtml): Long

    @Update
    abstract suspend fun updateContentHtml(contentHtml: ContentHtml): Int

    @Query("DELETE FROM content_html  WHERE linkId = :linkId")
    abstract suspend fun deleteContentHtml(linkId: Int): Int

    @Query("SELECT * FROM content_html WHERE linkId = :linkId")
    abstract fun getContentHtmlLiveData(linkId: Int): LiveData<ContentHtml?>

    @Query("SELECT * FROM content_html WHERE linkId = :linkId")
    abstract suspend fun getContentHtml(linkId: Int): ContentHtml?

    @Query("DELETE FROM content_html WHERE linkId = :linkId")
    abstract suspend fun deleteContentHtmlByLinkId(linkId: Int): Int
}
