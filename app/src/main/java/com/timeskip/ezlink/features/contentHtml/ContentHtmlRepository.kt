package com.timeskip.ezlink.features.contentHtml

import androidx.lifecycle.LiveData

interface ContentHtmlRepository {
    fun getContentHtmlLiveData(linkId: Int): LiveData<ContentHtml?>

    suspend fun getContentHtml(linkId: Int): ContentHtml?

    suspend fun insertContentHtml(contentHtml: ContentHtml): Boolean

    suspend fun updateContentHtml(contentHtml: ContentHtml): Boolean

    suspend fun deleteContentHtml(linkId: Int): Boolean

    suspend fun deleteContentHtmlByLinkId(linkId: Int): Boolean
}