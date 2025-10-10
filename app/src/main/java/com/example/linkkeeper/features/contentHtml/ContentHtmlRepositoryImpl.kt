package com.example.linkkeeper.features.contentHtml

import androidx.lifecycle.LiveData
import javax.inject.Inject

class ContentHtmlRepositoryImpl @Inject constructor(private val contentHtmlDao: ContentHtmlDao) :
    ContentHtmlRepository {
    override fun getContentHtmlLiveData(linkId: String): LiveData<ContentHtml?> =
        contentHtmlDao.getContentHtmlLiveData(linkId)

    override suspend fun insertContentHtml(contentHtml: ContentHtml): Boolean =
        contentHtmlDao.insertContentHtml(contentHtml) > 0
}