package com.example.myapplication.chapter_2.socket

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 *    @author wangruixiang
 *    @date 2021/4/13 7:55 PM
 */
class TCPServerService : Service() {
    companion object {
        const val TAG = "wangruixiang"
    }

    private var mIsServerDestroyed = false
    private val mDefinedMessages = arrayOf(
        "你好啊，哈哈",
        "请问你叫什么名字呀？",
        "今天北京天气不错啊，shy",
        "你知道吗？我可是可以和多个人同时聊天的哦",
        "给你讲个笑话吧：据说爱笑的人运气不会太差，不知道真假。"
    )

    override fun onCreate() {
        super.onCreate()
        Thread(TcpServer()).start()
    }

    override fun onDestroy() {
        mIsServerDestroyed = true
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private inner class TcpServer : Runnable {
        override fun run() {
            val serverSocket: ServerSocket?
            try {
                serverSocket = ServerSocket()
                serverSocket.bind(InetSocketAddress("127.0.0.1", 8688))
            } catch (e: IOException) {
                Log.e(TAG, "TCPServerService.run(), establish tcp server failed, port: 8688")
                e.printStackTrace()
                return
            }
            while (!mIsServerDestroyed) {
                val client: Socket = serverSocket.accept()
                thread {
                    try {
                        responseClient(client)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        @Throws(IOException::class)
        private fun responseClient(client: Socket) {
            val inStream = BufferedReader(InputStreamReader(client.getInputStream()))
            val outStream =
                PrintWriter(BufferedWriter(OutputStreamWriter(client.getOutputStream())), true)
            outStream.println("欢迎来到聊天室！")
            while (!mIsServerDestroyed) {
                val msg: String? = inStream.readLine()
                Log.i(TAG, "TCPServerService.responseClient(), msg from client: $msg")
                if (msg == null) {
                    break
                }
                val i = Random(System.currentTimeMillis()).nextInt(mDefinedMessages.size)
                val reply = mDefinedMessages[i]
                outStream.println(reply)
            }
            Log.i(TAG, "TCPServerService.responseClient(), client quit.")
            inStream.close()
            outStream.close()
            client.close()
        }
    }
}