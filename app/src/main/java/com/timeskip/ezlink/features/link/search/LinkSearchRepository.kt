package com.timeskip.ezlink.features.link.search

import androidx.lifecycle.LiveData
import com.timeskip.ezlink.features.link.data.Link

interface LinkSearchRepository {
    suspend fun search(query: String): List<Link>

    fun searchLiveData(searchQuery: String, limit: Int): LiveData<List<Link>>

    suspend fun deleteLink(link: Link): Boolean
}