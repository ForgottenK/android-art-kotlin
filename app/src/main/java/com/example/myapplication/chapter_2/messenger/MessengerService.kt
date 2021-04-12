package com.example.myapplication.chapter_2.messenger

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.example.myapplication.constants.MyConstants

/**
 *    @author wangruixiang
 *    @date 2021/4/12 2:32 PM
 */
class MessengerService : Service() {

    companion object {
        val TAG: String = MessengerService::class.java.simpleName
    }

    class MessengerHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MyConstants.MSG_FROM_CLIENT -> {
                    Log.i(TAG, "receive msg from client: " + msg.data.getString("msg"))
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private val mMessenger = Messenger(MessengerHandler())

    override fun onBind(intent: Intent?): IBinder? {
        return mMessenger.binder
    }

}