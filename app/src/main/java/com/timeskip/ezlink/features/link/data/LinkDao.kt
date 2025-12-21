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

    @Query("SELECT * FROM link Where tagName = :tagName")
    abstract fun getLinkListLiveData(tagName: String): LiveData<List<Link>>

    @Query("SELECT * FROM link Where tagName = :tagName")
    abstract fun getLinkList(tagName: String): List<Link>

    @Query("SELECT * FROM link WHERE tagName = :tagName LIMIT :limit OFFSET :offset")
    abstract suspend fun getLinks(tagName: String, offset: Int, limit: Int): List<Link>

    @Query(
        """
        SELECT link.*
        FROM link
        JOIN link_fts ON link_fts.rowid = link.id
        WHERE link_fts MATCH :query
        LIMIT :limit OFFSET :offset
    """
    )
    abstract suspend fun searchPaged(query: String, offset: Int, limit: Int): List<Link>

    @Query("DELETE FROM link Where tagName = :tagName")
    abstract fun deleteLinksByTagName(tagName: String): Int

    @Query(
        """
        SELECT link.*
        FROM link
        JOIN link_fts ON link_fts.rowid = link.id
        WHERE link_fts MATCH :query
    """
    )
    abstract suspend fun search(query: String): List<Link>

}