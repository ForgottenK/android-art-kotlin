package com.example.myapplication.chapter_2.binderpool

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.myapplication.aidl.IBinderPool

/**
 *    @author wangruixiang
 *    @date 2021/4/14 9:31 PM
 */
class BinderPoolService : Service() {
    companion object {
        const val TAG = "wangruixiang"
        const val BINDER_COMPUTE = 0
        const val BINDER_SECURITY_CENTER = 1
    }

    private val binderPool = BinderPoolImpl()

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "BinderPoolService.onBind()")
        return binderPool
    }

    class BinderPoolImpl : IBinderPool.Stub() {

        // 加个缓存，避免每次都创建实例
        private var computeImpl: ComputeImpl? = null
        private var securityCenterImpl: SecurityCenterImpl? = null

        override fun queryBinder(binderCode: Int): IBinder? =
            when (binderCode) {
                BINDER_COMPUTE -> computeImpl ?: ComputeImpl().also { this.computeImpl = it }
                BINDER_SECURITY_CENTER -> securityCenterImpl
                    ?: SecurityCenterImpl().also { this.securityCenterImpl = it }
                else -> null
            }
    }
}