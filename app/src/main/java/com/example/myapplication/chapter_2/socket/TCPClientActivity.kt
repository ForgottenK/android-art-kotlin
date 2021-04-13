package com.example.myapplication.chapter_2.socket

import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/**
 *    @author wangruixiang
 *    @date 2021/4/13 8:43 PM
 */
class TCPClientActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG = "wangruixiang"
        private const val MESSAGE_RECEIVE_NEW_MSG = 1
        private const val MESSAGE_SOCKET_CONNECTED = 2
        private const val MESSAGE_CLEAR_INPUT = 3
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.i(TAG, "TCPClientActivity.handleMessage, msg = $msg")
            when (msg.what) {
                MESSAGE_RECEIVE_NEW_MSG -> mMessageTextView.text =
                    "${mMessageTextView.text}${msg.obj as String}"
                MESSAGE_SOCKET_CONNECTED -> mSendButton.isEnabled = true
                MESSAGE_CLEAR_INPUT -> mMessageEditText.setText("")
            }
        }
    }
    private lateinit var mSendButton: Button
    private lateinit var mMessageTextView: TextView
    private lateinit var mMessageEditText: EditText

    private var mClientSocket: Socket? = null
    private var mPrintWriter: PrintWriter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcpclient)
        mSendButton = findViewById(R.id.activity_tcpclient_send)
        mSendButton.setOnClickListener(this)
        mMessageTextView = findViewById(R.id.activity_tcpclient_msg_container)
        mMessageEditText = findViewById(R.id.activity_tcpclient_msg)
        val service = Intent(this, TCPServerService::class.java)
        startService(service)
        thread {
            connectTCPServer()
        }
    }

    override fun onDestroy() {
        try {
            mClientSocket?.shutdownInput()
            mClientSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        if (v == mSendButton) {
            val msg = mMessageEditText.text.toString()
            if (msg.isNotEmpty()) {
                thread {
                    mPrintWriter?.println(msg)
                    mHandler.sendEmptyMessage(MESSAGE_CLEAR_INPUT)
                    val showedMsg = "self ${formatDateTime(System.currentTimeMillis())}: $msg\n"
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showedMsg).sendToTarget()
                }
            }
        }
    }

    private fun connectTCPServer() {
        var socket: Socket? = null
        while (socket == null) {
            try {
                socket = Socket()
                socket.connect(InetSocketAddress("127.0.0.1", 8688), 5000)
                mClientSocket = socket
                mPrintWriter =
                    PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED)
                Log.i(TAG, "TCPClientActivity.connectTCPServer(), connect server success")
            } catch (e: Exception) {
                e.printStackTrace()
                SystemClock.sleep(1000)
                Log.e(
                    TAG,
                    "TCPClientActivity.connectTCPServer(), connect tcp server failed, retry..."
                )
                socket = null
            }
        }

        try {
            val br = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (!isFinishing) {
                val msg: String? = br.readLine()
                Log.i(TAG, "TCPClientActivity.connectTCPServer(), receive: $msg")
                if (msg != null) {
                    val showedMsg = "server ${formatDateTime(System.currentTimeMillis())}: $msg\n"
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showedMsg).sendToTarget()
                }
            }

            Log.i(TAG, "TCPClientActivity.connectTCPServer(), quit...")
            mPrintWriter?.close()
            br.close()
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun formatDateTime(time: Long): String {
        return SimpleDateFormat.getTimeInstance().format(Date(time))
    }
}