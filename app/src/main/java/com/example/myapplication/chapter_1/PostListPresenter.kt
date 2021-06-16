package com.example.myapplication.chapter_1

import android.util.Log
import kotlinx.coroutines.*
import java.util.*

/**
 *    @author wangruixiang
 *    @date 2021/6/15 8:36 PM
 */
class PostListPresenter(var iPostListView: IPostListView?) : IPostListPresenter {

    private lateinit var requestPostListJob: Job

    override fun requestPostListData() {
        requestPostListJob = GlobalScope.launch {
            val postList = getPostList()
            withContext(Dispatchers.Main) {
                iPostListView?.onReceivePostListData(postList)
            }
        }
    }

    private suspend fun getPostList(): List<Post> {
        val postList = mutableListOf<Post>()
        val databasePosts = readPostListFromDB()
        postList.addAll(databasePosts)

        val networkPosts = getPostListFromNetwork()
        savePostListToDB(networkPosts)

        postList.addAll(networkPosts)
        return postList
    }

    private suspend fun getPostListFromNetwork(): MutableList<Post> {
        val users = listOf("frank", "bob", "mary", "eric", "kevin", "tom", "justin")
        val dates = generateDates()
        val networkPosts = mutableListOf<Post>()
        for (i in 1..10) {
            networkPosts.add(Post(i, users.random(), "Post #$i", dates.random()))
        }
        delay(10000)
        return networkPosts
    }

    private suspend fun savePostListToDB(postList: List<Post>) {
        Log.i(TAG, "PostListPresenter.savePostListToDB(), postList.size = ${postList.size}")
        iPostListView?.let { view ->
            view.getDatabaseContext()?.let { context ->
                withContext(Dispatchers.IO) {
                    val postDao = AppDatabase.getInstance(context).postDao()
                    postDao.deleteAllPosts()
                    for (post in postList) {
                        postDao.insertPosts(post)
                    }
                }
            }
        }
    }

    private fun readPostListFromDB(): List<Post> {
        val result = mutableListOf<Post>()
        iPostListView?.let { view ->
            view.getDatabaseContext()?.let { context ->
                val postDao = AppDatabase.getInstance(context).postDao()
                val posts = postDao.getAllPosts()
                Log.i(TAG, "PostListPresenter.readPostListFromDB(), posts.size = ${posts.size}")
                result.addAll(posts)
            }
        }
        Log.i(TAG, "PostListPresenter.readPostListFromDB(), result.size = ${result.size}")
        return result
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

    override fun onDestroy() {
        if (requestPostListJob.isActive) {
            requestPostListJob.cancel()
        }
        iPostListView = null
    }

    companion object {
        const val TAG = "wangruixiang"
    }
}

interface IPostListPresenter {
    fun requestPostListData()
    fun onDestroy()
}