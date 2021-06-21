package com.example.myapplication

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.chapter_1.model.db.AppDatabase
import com.example.myapplication.chapter_1.model.db.PostDao
import com.example.myapplication.chapter_1.model.entity.Post
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 *    @author wangruixiang
 *    @date 2021/6/21 10:17 PM
 */
@RunWith(AndroidJUnit4::class)
class PostDaoTest {
    private lateinit var postDao: PostDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        postDao = db.postDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPost() = runBlocking {
        val post = Post(1, "master", "post #1")
        postDao.insertPosts(post)
        val allPosts = postDao.getAllPosts()
        assertEquals(post, allPosts[0])
    }

    @Test
    @Throws(Exception::class)
    fun getAllPosts() = runBlocking {
        val post1 = Post(1, "master", "post #1")
        val post2 = Post(2, "bob", "post #2")
        postDao.insertPosts(post1, post2)
        val allPosts = postDao.getAllPosts()
        assertEquals(post1, allPosts[0])
        assertEquals(post2, allPosts[1])
    }

    @Test
    @Throws(Exception::class)
    fun deleteAll() = runBlocking {
        val post1 = Post(1, "master", "post #1")
        val post2 = Post(2, "bob", "post #2")
        val posts = arrayOf(post1, post2)
        postDao.insertPosts(*posts)
        postDao.deleteAllPosts()
        val allPosts = postDao.getAllPosts()
        assertTrue(allPosts.isEmpty())
    }
}