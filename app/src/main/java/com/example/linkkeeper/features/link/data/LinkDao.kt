package com.example.linkkeeper.features.link.data

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

    @Query("SELECT * FROM link Where tagId = :tagId")
    abstract fun getLinkListLiveData(tagId: String): LiveData<List<Link>>

    @Query("SELECT * FROM link Where tagId = :tagId")
    abstract fun getLinkList(tagId: String): List<Link>

    @Query("""
        SELECT link.*
        FROM link
        JOIN link_fts ON link_fts.title = link.title
        WHERE link_fts MATCH :query
    """)
    abstract suspend fun search(query: String): List<Link>
    
}