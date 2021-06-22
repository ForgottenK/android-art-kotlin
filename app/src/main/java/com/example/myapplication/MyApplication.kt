package com.example.myapplication

import android.app.Application
import com.example.myapplication.chapter_1.model.db.AppDatabase
import com.example.myapplication.chapter_1.model.repository.PostRepository
import com.example.myapplication.chapter_13.CrashHandler
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/**
 *    @author wangruixiang
 *    @date 2021/4/27 4:33 PM
 */

class MyApplication : Application() {

    val appDatabase by lazy { AppDatabase.getInstance(this) }
    val postRepository by lazy { PostRepository(appDatabase.postDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        val crashHandler = CrashHandler.instance
        crashHandler.init(this)

        initOtherModules()
    }

    private fun initOtherModules() {
        val countDownLatch = CountDownLatch(3)
        val newFixedThreadPool = Executors.newFixedThreadPool(4)
        newFixedThreadPool.submit {
            initModuleA()
        }
        newFixedThreadPool.submit {
            initModuleD()
            countDownLatch.countDown()
        }
        newFixedThreadPool.submit {
            initModuleF()
            countDownLatch.countDown()
        }
        newFixedThreadPool.submit {
            initModuleG()
            countDownLatch.countDown()
        }
        countDownLatch.await()
        initModuleB()
        initModuleC()
        initModuleE()
    }

    private fun initModuleA() {
        Thread.sleep(100)
    }

    private fun initModuleB() {
        Thread.sleep(10)
    }

    private fun initModuleC() {
        Thread.sleep(30)
    }

    private fun initModuleD() {
        Thread.sleep(200)
    }

    private fun initModuleE() {
        Thread.sleep(50)
    }

    private fun initModuleF() {
        Thread.sleep(300)
    }

    private fun initModuleG() {
        Thread.sleep(500)
    }

    companion object {
        var instance: MyApplication? = null
            private set
        var userCreatedId = -1
            private set
            get() = field--
    }
}