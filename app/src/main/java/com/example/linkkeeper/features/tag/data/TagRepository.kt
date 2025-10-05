package com.example.linkkeeper.features.tag.data

import androidx.lifecycle.LiveData
import androidx.room.Query

interface TagRepository {

    @Query("SELECT * FROM tag WHERE isDefaultCreated = 1")
    fun getDefaultTagListLiveData(): LiveData<List<Tag>>

    @Query("SELECT * FROM tag WHERE isDefaultCreated = 0")
    fun getUserTagListLiveData(): LiveData<List<Tag>>

    fun getAllTagListLiveData(): LiveData<List<Tag>>

    suspend fun getTagList(): List<Tag>

    suspend fun createTag(tag: Tag): Boolean

    suspend fun deleteTag(tag: Tag): Boolean

    suspend fun updateTag(tag: Tag): Boolean
}