package com.timeskip.ezlink.features.link.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.timeskip.ezlink.features.common.ImageStorageHelper
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

    override fun getLinksLiveData(tagName: String, searchQuery: String, limit: Int): LiveData<List<Link>> =
        linkDao.getLinksLiveData(tagName, searchQuery, limit)

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

    override suspend fun deleteLink(context: Context, link: Link): Boolean {
        // Delete stored image if it's a local file
        if (ImageStorageHelper.isLocalStoredImage(context, link.url)) {
            ImageStorageHelper.deleteImage(context, link.url)
        }

        databaseTransactionRunner.withTransaction {
            linkDao.deleteLink(link)
            contentHtmlDao.deleteContentHtml(link.id ?: return@withTransaction)
        }
        return true
    }

    override fun getPagedLinkListLiveData(tagName: String, pageSize: Int): LiveData<List<Link>> {
        val liveData = MutableLiveData<List<Link>>()
        // Start with first page; callers will explicitly request more items
        liveData.value = emptyList()
        return liveData
    }

    override suspend fun getLinks(tagName: String, offset: Int, limit: Int): List<Link> =
        linkDao.getLinks(tagName, offset, limit)

    override suspend fun searchPaged(query: String, offset: Int, limit: Int): List<Link> =
        linkDao.searchPaged(query, offset, limit)
}