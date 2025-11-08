package com.timeskip.ezlink.features.db

interface DatabaseTransactionRunner {
    suspend fun withTransaction(block: suspend () -> Unit): Unit
}