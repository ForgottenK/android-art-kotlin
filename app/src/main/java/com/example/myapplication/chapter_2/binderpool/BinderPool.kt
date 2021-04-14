package com.example.myapplication.chapter_2.binderpool

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.myapplication.aidl.IBinderPool
import java.util.concurrent.CountDownLatch

/**
 *    @author wangruixiang
 *    @date 2021/4/14 9:32 PM
 */
class BinderPool private constructor(context: Context) {
    companion object {
        private const val TAG = "wangruixiang"

        // companion object 实现带参单例 https://xiaozhuanlan.com/topic/7368925041
        @Volatile
        private var INSTANCE: BinderPool? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: BinderPool(context).also {
                    it.connectBinderPoolService()
                    INSTANCE = it
                }
            }
    }

    private val context = context.applicationContext
    private var binderPool: IBinderPool? = null

    // 使用 CountDownLatch 将异步绑定服务的过程变为同步调用
    // 服务绑定成功，connectBinderPoolService() 函数才退出
    private var syncCountDownLatch: CountDownLatch? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            // ignored
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binderPool = IBinderPool.Stub.asInterface(service)
            try {
                binderPool?.asBinder()?.linkToDeath(deathRecipient, 0)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            syncCountDownLatch?.countDown()
        }
    }

    private val deathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            Log.w(TAG, "BinderPool.DeathRecipient, binder died.")
            binderPool?.asBinder()?.unlinkToDeath(this, 0)
            binderPool = null
            connectBinderPoolService()
        }
    }

    private fun connectBinderPoolService() {
        synchronized(this) {
            syncCountDownLatch = CountDownLatch(1)
            val intent = Intent(context, BinderPoolService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            syncCountDownLatch!!.await()
        }
    }

    fun queryBinder(binderCode: Int): IBinder? = try {
        binderPool?.queryBinder(binderCode)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * object + getInstance() 通过依赖注入实现带参单例
 */
object BinderPoolObject {
    private var context: Context? = null

    fun getInstance(context: Context): BinderPoolObject {
        if (this.context == null) {
            this.context = context.applicationContext
            // init...
        }
        return this
    }
}