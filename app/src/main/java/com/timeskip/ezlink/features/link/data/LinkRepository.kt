package com.timeskip.ezlink.features.link.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.timeskip.ezlink.features.tag.data.Tag

interface LinkRepository {
    suspend fun getTag(tagName: String): Tag

    fun getLinkListLiveData(tagName: String): LiveData<List<Link>>

    fun getAllLink(tagName: String): List<Link>

    suspend fun insertLink(link: Link): Boolean

    suspend fun updateLink(link: Link): Boolean

    suspend fun search(query: String): List<Link>

    suspend fun deleteLink(context: Context, link: Link): Boolean

    fun getPagedLinkListLiveData(tagName: String, pageSize: Int = 20): LiveData<List<Link>>

    suspend fun getLinks(tagName: String, offset: Int, limit: Int): List<Link>

    suspend fun searchPaged(query: String, offset: Int, limit: Int): List<Link>
}