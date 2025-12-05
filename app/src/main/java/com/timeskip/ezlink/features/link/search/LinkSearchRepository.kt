package com.timeskip.ezlink.features.link.search

import com.timeskip.ezlink.features.link.data.Link

interface LinkSearchRepository {
    suspend fun search(query: String): List<Link>

    suspend fun deleteLink(link: Link): Boolean
}