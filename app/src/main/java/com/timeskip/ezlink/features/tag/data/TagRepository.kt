package com.timeskip.ezlink.features.tag.data

import androidx.lifecycle.LiveData

interface TagRepository {
    fun getTagListLiveData(): LiveData<List<Tag>>

    suspend fun getTagList(): List<Tag>

    suspend fun createTag(tag: Tag): Boolean

    suspend fun deleteTag(tag: Tag): Boolean

    suspend fun getTagByName(name: String): Tag?
}