package com.timeskip.ezlink.features.contentHtml

import javax.inject.Inject

class ContentHtmlRepositoryImpl @Inject constructor(
    private val contentHtmlDao: ContentHtmlDao
) : ContentHtmlRepository {
    override fun getContentHtmlLiveData(linkId: Int) = contentHtmlDao.getContentHtmlLiveData(linkId)

    override suspend fun getContentHtml(linkId: Int): ContentHtml? =
        contentHtmlDao.getContentHtml(linkId)

    override suspend fun insertContentHtml(contentHtml: ContentHtml): Boolean {
        return contentHtmlDao.insertContentHtml(contentHtml) > 0
    }

    override suspend fun updateContentHtml(contentHtml: ContentHtml): Boolean {
        return contentHtmlDao.updateContentHtml(contentHtml) > 0
    }

    override suspend fun deleteContentHtml(linkId: Int): Boolean {
        return contentHtmlDao.deleteContentHtml(linkId) > 0
    }

    override suspend fun deleteContentHtmlByLinkId(linkId: Int): Boolean {
        return contentHtmlDao.deleteContentHtmlByLinkId(linkId) > 0
    }
}