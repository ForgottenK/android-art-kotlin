package com.example.myapplication.chapter_1.model.db

import android.util.Log
import com.example.myapplication.chapter_1.model.entity.Post
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

/**
 *    @author wangruixiang
 *    @date 2021/6/17 1:45 AM
 */
class PostRepository(private val postDao: PostDao) {

    fun getPosts(): Flow<List<Post>> {
        return flow {
            emit(postDao.getAllPosts())
            emit(getPostListFromNetwork())
        }
    }

    suspend fun savePostsToDB(posts: List<Post>) {
        Log.i(TAG, "PostRepository.savePostListToDB(), posts.size = ${posts.size}")
        postDao.deleteAllPosts()
        for (post in posts) {
            postDao.insertPosts(post)
        }
    }

    suspend fun deleteAllPostsInDB() {
        postDao.deleteAllPosts()
    }

    private suspend fun getPostListFromNetwork(): List<Post> {
        val users = listOf("frank", "bob", "mary", "eric", "kevin", "tom", "justin")
        val dates = generateDates()
        val networkPosts = mutableListOf<Post>()
        for (i in 1..30) {
            networkPosts.add(Post(i, users.random(), "Post #$i", dates.random()))
        }
        delay(10000)
        savePostsToDB(networkPosts)
        return networkPosts
    }

    private fun generateDates(): List<Date> {
        val dates = mutableListOf<Date>()

        val calendar = GregorianCalendar()
        dates.add(calendar.time)
        calendar.add(Calendar.YEAR, -1)
        dates.add(calendar.time)
        calendar.add(Calendar.MONTH, 6)
        dates.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 12)
        dates.add(calendar.time)
        calendar.add(Calendar.MONTH, 3)
        dates.add(calendar.time)

        return dates
    }

    companion object {
        const val TAG = "wangruixiang"
    }

}