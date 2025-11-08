package com.timeskip.ezlink.features.link.data

import androidx.lifecycle.LiveData
import com.timeskip.ezlink.features.tag.data.Tag

interface LinkRepository {
    suspend fun getTag(tagName: String): Tag

    fun getLinkListLiveData(tagName: String): LiveData<List<Link>>

    fun getAllLink(tagName: String): List<Link>

    suspend fun insertLink(link: Link): Boolean

    suspend fun updateLink(link: Link): Boolean

    suspend fun search(query: String): List<Link>

    suspend fun deleteLink(link: Link): Boolean
}