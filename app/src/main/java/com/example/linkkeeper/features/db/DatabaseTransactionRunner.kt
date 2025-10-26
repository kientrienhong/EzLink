package com.example.linkkeeper.features.db

interface DatabaseTransactionRunner {
    suspend fun withTransaction(block: suspend () -> Unit): Unit
}