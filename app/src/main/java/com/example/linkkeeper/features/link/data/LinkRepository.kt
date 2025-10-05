package com.example.linkkeeper.features.link.data

import androidx.lifecycle.LiveData
import com.example.linkkeeper.features.tag.data.Tag

interface LinkRepository {
    suspend fun getTag(id: String): Tag

    fun getLinkListLiveData(tagId: String): LiveData<List<Link>>

    fun getAllLink(tagId: String): List<Link>

    suspend fun insertLink(link: Link): Boolean

    suspend fun updateLink(link: Link): Boolean

    suspend fun search(query: String): List<Link>

    suspend fun deleteLink(link: Link): Boolean

    // ContentHtml operations
    suspend fun insertContentHtml(contentHtml: ContentHtml): Boolean

    suspend fun updateContentHtml(contentHtml: ContentHtml): Boolean

    suspend fun deleteContentHtml(contentHtml: ContentHtml): Boolean

    suspend fun getContentHtml(linkId: Int): ContentHtml?

    fun getContentHtmlLiveData(linkId: Int): LiveData<ContentHtml?>

    suspend fun deleteContentHtmlByLinkId(linkId: Int): Boolean
}