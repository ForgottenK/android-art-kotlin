package com.example.myapplication.chapter_1

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
        delay(500)
        val users = listOf("frank", "bob", "mary", "eric", "kevin", "tom", "justin")
        val dates = generateDates()

        val postList = mutableListOf<Post>()
        for (i in 1..50) {
            postList.add(Post(i.toString(), users.random(), "Post #$i", dates.random()))
        }
        return postList
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
}

interface IPostListPresenter {
    fun requestPostListData()
    fun onDestroy()
}