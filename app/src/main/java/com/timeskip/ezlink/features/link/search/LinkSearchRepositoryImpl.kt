package com.timeskip.ezlink.features.link.search

import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkDao
import javax.inject.Inject

class LinkSearchRepositoryImpl @Inject constructor(
    private val linkDao: LinkDao,
) : LinkSearchRepository {
    override suspend fun search(query: String): List<Link> = linkDao.search(query)

    override suspend fun deleteLink(link: Link): Boolean = linkDao.deleteLink(link) > 0
}