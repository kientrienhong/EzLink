package com.example.linkkeeper.features.tag.data

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
        SELECT *, (SELECT sum(tagId) from link where link.tagId = tag.id) as amountOfLink
        FROM tag
    """
    )
    abstract suspend fun getTagList(): List<Tag>

    @Query("SELECT * from tag where id = :id")
    abstract suspend fun getTag(id: String): Tag

    @Query(
        """
        SELECT *, (SELECT count(tagId) from link where link.tagId = tag.id) as amountOfLink
        FROM tag
        WHERE isDefaultCreated = 1
    """
    )
    abstract fun getDefaultTagListLiveData(): LiveData<List<Tag>>

    @Query(
        """
        SELECT *, (SELECT count(tagId) from link where link.tagId = tag.id) as amountOfLink
        FROM tag
        WHERE isDefaultCreated = 0
    """
    )
    abstract fun getUserTagListLiveData(): LiveData<List<Tag>>

    @Query(
        """
        SELECT *, (SELECT count(tagId) from link where link.tagId = tag.id) as amountOfLink
        FROM tag
    """
    )
    abstract fun getAllTagListLiveData(): LiveData<List<Tag>>

    @Insert
    abstract suspend fun insertTag(tag: Tag): Long

    @Delete
    abstract suspend fun deleteTag(tag: Tag): Int

    @Query("DELETE FROM tag")
    abstract suspend fun deleteTagList()

    @Update
    abstract suspend fun updateTag(tag: Tag): Int
}