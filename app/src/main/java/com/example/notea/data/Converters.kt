package com.example.notea.data

import androidx.room.TypeConverter
import com.example.notea.models.Priority
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromPriority(value: String): Priority {
        return Priority.valueOf(value)
    }

    @TypeConverter
    fun toPriority(priority: Priority): String {
        return priority.name
    }
}