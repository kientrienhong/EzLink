package com.example.linkkeeper.features.contentHtml

import androidx.lifecycle.LiveData

interface ContentHtmlRepository {
    fun getContentHtmlLiveData(linkId: String): LiveData<ContentHtml?>

    suspend fun insertContentHtml(contentHtml: ContentHtml): Boolean
}