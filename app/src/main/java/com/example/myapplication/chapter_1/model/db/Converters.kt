package com.example.myapplication.chapter_1.model.db

import androidx.room.TypeConverter
import java.util.*

/**
 *    @author wangruixiang
 *    @date 2021/6/16 1:34 PM
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}