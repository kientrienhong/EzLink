package com.example.linkkeeper.features.tag.data

import androidx.lifecycle.LiveData
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(private val tagDao: TagDao) : TagRepository {
    override fun getDefaultTagListLiveData(): LiveData<List<Tag>> =
        tagDao.getDefaultTagListLiveData()

    override fun getUserTagListLiveData(): LiveData<List<Tag>> = tagDao.getUserTagListLiveData()

    override fun getAllTagListLiveData(): LiveData<List<Tag>> = tagDao.getAllTagListLiveData()

    override suspend fun getTagList(): List<Tag> = tagDao.getTagList()

    override suspend fun createTag(tag: Tag): Boolean = tagDao.insertTag(tag) > 0

    override suspend fun deleteTag(tag: Tag): Boolean = tagDao.deleteTag(tag) > 0

    override suspend fun updateTag(tag: Tag): Boolean = tagDao.updateTag(tag) > 0
}