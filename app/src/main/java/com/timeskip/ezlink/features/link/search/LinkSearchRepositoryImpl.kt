package com.timeskip.ezlink.features.link.search

import androidx.lifecycle.LiveData
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkDao
import javax.inject.Inject

class LinkSearchRepositoryImpl @Inject constructor(
    private val linkDao: LinkDao,
) : LinkSearchRepository {
    override suspend fun search(query: String): List<Link> = linkDao.search(query)

    override fun searchLiveData(searchQuery: String, limit: Int): LiveData<List<Link>> =
        linkDao.searchLiveData(searchQuery, limit)

    override suspend fun deleteLink(link: Link): Boolean = linkDao.deleteLink(link) > 0
}