package com.timeskip.ezlink.features.db

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import javax.inject.Inject

class RoomDatabaseTransactionRunner @Inject constructor(
    private val db: RoomDatabase
) : DatabaseTransactionRunner {
    override suspend fun withTransaction(block: suspend () -> Unit): Unit =
        db.withTransaction {
            block()
        }
}