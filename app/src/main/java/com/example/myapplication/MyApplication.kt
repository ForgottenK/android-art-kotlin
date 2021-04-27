package com.example.myapplication

import android.app.Application
import com.example.myapplication.chapter13.CrashHandler

/**
 *    @author wangruixiang
 *    @date 2021/4/27 4:33 PM
 */

class TestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        val crashHandler = CrashHandler.instance
        crashHandler.init(this)
    }

    companion object {
        var instance: TestApp? = null
            private set
    }
}