package com.timeskip.ezlink.features.link.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
abstract class LinkDao {
    @Insert
    abstract suspend fun insertLink(link: Link): Long

    @Update
    abstract suspend fun updateLink(link: Link): Int

    @Delete
    abstract suspend fun deleteLink(link: Link): Int

    /**
     * Unified query method that supports both tag filtering and FTS search
     * Returns LiveData that auto-updates when data changes
     *
     * @param tagName Tag to filter by
     * @param searchQuery FTS search query (empty string means no search, just filter by tag)
     * @param limit Maximum number of results (for pagination)
     */
    @Query(
        """
        SELECT link.*
        FROM link
        LEFT JOIN link_fts ON link_fts.rowid = link.id
        WHERE link.tagName = :tagName
          AND (CASE WHEN :searchQuery = '' THEN 1 ELSE link_fts MATCH :searchQuery END)
        ORDER BY link.dateOfCreated DESC
        LIMIT :limit
    """
    )
    abstract fun getLinksLiveData(
        tagName: String,
        searchQuery: String,
        limit: Int
    ): LiveData<List<Link>>

    @Query("DELETE FROM link Where tagName = :tagName")
    abstract fun deleteLinksByTagName(tagName: String): Int

    /**
     * Global search with LiveData support (no tag filter)
     * @param searchQuery FTS search query
     * @param limit Maximum number of results (for pagination)
     */
    @Query(
        """
        SELECT link.*
        FROM link
        JOIN link_fts ON link_fts.rowid = link.id
        WHERE link_fts MATCH :searchQuery
        ORDER BY link.dateOfCreated DESC
        LIMIT :limit
    """
    )
    abstract fun searchLiveData(searchQuery: String, limit: Int): LiveData<List<Link>>

}