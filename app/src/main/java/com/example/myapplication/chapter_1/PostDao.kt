package com.example.myapplication.chapter_1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 *    @author wangruixiang
 *    @date 2021/6/16 12:38 PM
 */
@Dao
interface PostDao {
    @Query("SELECT * FROM post_table ORDER BY id ASC")
    fun getAllPosts(): List<Post>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPosts(vararg posts: Post)

    @Query("DELETE FROM post_table")
    suspend fun deleteAllPosts()
}