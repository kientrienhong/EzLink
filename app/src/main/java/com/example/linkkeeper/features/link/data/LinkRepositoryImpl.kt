package com.example.linkkeeper.features.link.data

import androidx.lifecycle.LiveData
import com.example.linkkeeper.features.tag.data.Tag
import com.example.linkkeeper.features.tag.data.TagDao
import javax.inject.Inject

class LinkRepositoryImpl @Inject constructor(
    private val linkDao: LinkDao,
    private val tagDao: TagDao,
    private val contentHtmlDao: ContentHtmlDao
) : LinkRepository {
    override suspend fun getTag(id: Int): Tag = tagDao.getTag(id)

    override fun getLinkListLiveData(tagId: Int): LiveData<List<Link>> = linkDao.getLinkListLiveData(tagId)

    override fun getAllLink(tagId: Int): List<Link> = linkDao.getLinkList(tagId)

    override suspend fun insertLink(link: Link): Boolean = linkDao.insertLink(link) > 0

    override suspend fun updateLink(link: Link): Boolean = linkDao.updateLink(link) > 0

    override suspend fun search(query: String): List<Link> = linkDao.search(query)

    override suspend fun deleteLink(link: Link): Boolean = linkDao.deleteLink(link) > 0

    // ContentHtml operations
    override suspend fun insertContentHtml(contentHtml: ContentHtml): Boolean =
        contentHtmlDao.insertContentHtml(contentHtml) > 0

    override suspend fun updateContentHtml(contentHtml: ContentHtml): Boolean =
        contentHtmlDao.updateContentHtml(contentHtml) > 0

    override suspend fun deleteContentHtml(contentHtml: ContentHtml): Boolean =
        contentHtmlDao.deleteContentHtml(contentHtml) > 0

    override suspend fun getContentHtml(linkId: Int): ContentHtml? =
        contentHtmlDao.getContentHtml(linkId)

    override fun getContentHtmlLiveData(linkId: Int): LiveData<ContentHtml?> =
        contentHtmlDao.getContentHtmlLiveData(linkId)

    override suspend fun deleteContentHtmlByLinkId(linkId: Int): Boolean =
        contentHtmlDao.deleteContentHtmlByLinkId(linkId) > 0
}