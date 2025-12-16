package com.timeskip.ezlink.features.link.data

import androidx.lifecycle.LiveData
import com.timeskip.ezlink.features.contentHtml.ContentHtmlDao
import com.timeskip.ezlink.features.db.DatabaseTransactionRunner
import com.timeskip.ezlink.features.tag.data.Tag
import com.timeskip.ezlink.features.tag.data.TagDao
import javax.inject.Inject

class LinkRepositoryImpl @Inject constructor(
    private val linkDao: LinkDao,
    private val tagDao: TagDao,
    private val contentHtmlDao: ContentHtmlDao,
    private val databaseTransactionRunner: DatabaseTransactionRunner
) : LinkRepository {
    override suspend fun getTag(tagName: String): Tag = tagDao.getTagByName(tagName)

    override fun getLinkListLiveData(tagName: String): LiveData<List<Link>> =
        linkDao.getLinkListLiveData(tagName)

    override fun getAllLink(tagName: String): List<Link> = linkDao.getLinkList(tagName)

    override suspend fun insertLink(link: Link): Boolean {
        val result = linkDao.insertLink(link) > 0
        try {
            tagDao.updateTagUpdatedAt(link.tagName)
        } catch (_: Exception) {
            // ignore
        }
        return result
    }

    override suspend fun updateLink(link: Link): Boolean {
        val result = linkDao.updateLink(link) > 0
        try {
            tagDao.updateTagUpdatedAt(link.tagName)
        } catch (_: Exception) {
            // ignore
        }
        return result
    }

    override suspend fun search(query: String): List<Link> = linkDao.search(query)

    override suspend fun deleteLink(link: Link): Boolean {
        databaseTransactionRunner.withTransaction {
            linkDao.deleteLink(link)
            contentHtmlDao.deleteContentHtml(link.id ?: return@withTransaction)
        }
        return true
    }
}