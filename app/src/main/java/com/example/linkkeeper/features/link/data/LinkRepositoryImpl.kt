package com.example.linkkeeper.features.link.data

import androidx.lifecycle.LiveData
import com.example.linkkeeper.features.contentHtml.ContentHtmlDao
import com.example.linkkeeper.features.db.DatabaseTransactionRunner
import com.example.linkkeeper.features.tag.data.Tag
import com.example.linkkeeper.features.tag.data.TagDao
import javax.inject.Inject

class LinkRepositoryImpl @Inject constructor(
    private val linkDao: LinkDao,
    private val tagDao: TagDao,
    private val contentHtmlDao: ContentHtmlDao,
    private val databaseTransactionRunner: DatabaseTransactionRunner
) : LinkRepository {
    override suspend fun getTag(id: Int): Tag = tagDao.getTag(id)

    override fun getLinkListLiveData(tagId: Int): LiveData<List<Link>> =
        linkDao.getLinkListLiveData(tagId)

    override fun getAllLink(tagId: Int): List<Link> = linkDao.getLinkList(tagId)

    override suspend fun insertLink(link: Link): Boolean = linkDao.insertLink(link) > 0

    override suspend fun updateLink(link: Link): Boolean = linkDao.updateLink(link) > 0

    override suspend fun search(query: String): List<Link> = linkDao.search(query)

    override suspend fun deleteLink(link: Link): Boolean {
        databaseTransactionRunner.withTransaction {
            linkDao.deleteLink(link)
            contentHtmlDao.deleteContentHtml(link.id ?: return@withTransaction)
        }
        return true
    }
}