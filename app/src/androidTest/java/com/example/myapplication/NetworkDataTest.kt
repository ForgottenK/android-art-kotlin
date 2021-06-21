package com.example.myapplication

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.chapter_1.model.db.AppDatabase
import com.example.myapplication.chapter_1.model.db.PostDao
import com.example.myapplication.chapter_1.model.repository.PostRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 *    @author wangruixiang
 *    @date 2021/6/22 1:40 AM
 */
@RunWith(AndroidJUnit4::class)
class NetworkDataTest {
    private lateinit var postDao: PostDao
    private lateinit var db: AppDatabase
    private lateinit var postRepository: PostRepository

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        postDao = db.postDao()
        postRepository = PostRepository(postDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getNetworkPosts() = runBlocking {
        val posts = postRepository.testPostListFromNetwork()
        for (post in posts) {
            assertNotNull("post $post .likeCount is null!", post.likeCount)
        }
    }
}