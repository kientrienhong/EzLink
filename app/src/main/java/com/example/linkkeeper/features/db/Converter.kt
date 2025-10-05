package com.example.linkkeeper.features.db

import androidx.room.TypeConverter
import java.util.Date

object Converter {
    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }
}