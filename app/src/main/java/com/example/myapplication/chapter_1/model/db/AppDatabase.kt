package com.example.myapplication.chapter_1.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.chapter_1.model.entity.Post

/**
 *    @author wangruixiang
 *    @date 2021/6/16 1:27 PM
 */
@Database(entities = [Post::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    companion object {
        private const val DB_NAME = "app_database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            val applicationContext = context.applicationContext
            return Room.databaseBuilder(applicationContext, AppDatabase::class.java, DB_NAME)
                .build()
        }
    }

}