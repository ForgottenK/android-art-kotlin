package com.example.myapplication.chapter_11

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.JobIntentService

/**
 *    @author wangruixiang
 *    @date 2021/4/26 4:35 PM
 */
class LocalIntentService : JobIntentService() {
    companion object {
        const val TAG = "wangruixiang"
        private const val JOB_ID = 1000

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, LocalIntentService::class.java, JOB_ID, work)
        }
    }

    private var handler: Handler? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler = Handler(Looper.getMainLooper())
        toast("LocalIntentService started")
        if (intent != null) {
            enqueueWork(this, LocalIntentService::class.java, JOB_ID, intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleWork(intent: Intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        intent.getStringExtra("task_action")?.let {
            Log.d(TAG, "LocalIntentService.onHandleWork(), receive task: $it")
            SystemClock.sleep(3000)
            if ("com.wrx.action.TASK1" == it) {
                Log.d(TAG, "LocalIntentService.onHandleWork(), handle task: $it")
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "LocalIntentService.onDestroy(), service destroyed")
        toast("All work complete")
        super.onDestroy()
    }

    private fun toast(text: CharSequence) {
        handler?.post {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
}