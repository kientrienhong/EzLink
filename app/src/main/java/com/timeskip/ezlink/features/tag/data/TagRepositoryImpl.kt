package com.timeskip.ezlink.features.tag.data

import androidx.lifecycle.LiveData
import com.timeskip.ezlink.features.db.DatabaseTransactionRunner
import com.timeskip.ezlink.features.link.data.LinkDao
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val linkDao: LinkDao,
    private val databaseTransactionRunner: DatabaseTransactionRunner
) : TagRepository {
    override fun getTagListLiveData(): LiveData<List<Tag>> = tagDao.getTagListLiveData()

    override suspend fun getTagList(): List<Tag> = tagDao.getTagList()

    override suspend fun createTag(tag: Tag): Boolean = tagDao.insertTag(tag) > 0

    override suspend fun deleteTag(tag: Tag): Boolean {
        databaseTransactionRunner.withTransaction {
            linkDao.deleteLinksByTagName(tag.name)
            tagDao.deleteTag(tag)
        }
        return true
    }

    override suspend fun getTagByName(name: String): Tag? = tagDao.getTagByName(name)
}