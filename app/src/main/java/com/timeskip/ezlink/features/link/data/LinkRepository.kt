package com.timeskip.ezlink.features.link.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.timeskip.ezlink.features.tag.data.Tag

interface LinkRepository {
    suspend fun getTag(tagName: String): Tag

    /**
     * Unified LiveData method that supports both tag filtering and search
     * @param tagName Tag to filter by
     * @param searchQuery Search query (empty string = no search)
     * @param limit Maximum results for pagination
     */
    fun getLinksLiveData(tagName: String, searchQuery: String, limit: Int): LiveData<List<Link>>

    suspend fun insertLink(link: Link): Boolean

    suspend fun updateLink(link: Link): Boolean

    suspend fun deleteLink(context: Context, link: Link): Boolean

    fun getPagedLinkListLiveData(tagName: String, pageSize: Int = 20): LiveData<List<Link>>
}