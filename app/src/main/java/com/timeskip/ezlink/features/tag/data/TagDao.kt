package com.timeskip.ezlink.features.tag.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
abstract class TagDao {
    @Query(
        """
        SELECT *, (SELECT sum(tagName) from link where link.tagName = tag.name) as amountOfLink
        FROM tag
    """
    )
    abstract suspend fun getTagList(): List<Tag>

    @Query("SELECT * from tag where name = :name")
    abstract suspend fun getTagByName(name: String): Tag

    @Query(
        """
        SELECT *, (SELECT count(tagName) from link where link.tagName = tag.name) as amountOfLink
        FROM tag
    """
    )
    abstract fun getTagListLiveData(): LiveData<List<Tag>>

    @Insert
    abstract suspend fun insertTag(tag: Tag): Long

    @Delete
    abstract suspend fun deleteTag(tag: Tag): Int

    @Query("DELETE FROM tag")
    abstract suspend fun deleteTagList()

    @Update
    abstract suspend fun updateTag(tag: Tag): Int
}