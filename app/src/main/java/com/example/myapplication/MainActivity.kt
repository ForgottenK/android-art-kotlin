package com.example.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.chapter_2.messenger.MessengerService
import com.example.myapplication.constants.MyConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "wangruixiang"
    }

    private lateinit var mService: Messenger
    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "Service ${componentName?.shortClassName} is disconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = Messenger(service)
            val msg = Message.obtain(null, MyConstants.MSG_FROM_CLIENT)
            msg.data = Bundle().also { b -> b.putString("msg", "hello, this is client.") }
            msg.replyTo = mGetReplyMessenger
            try {
                mService.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
    private val mGetReplyMessenger = Messenger(MessengerHandler())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            val intent = Intent(this@MainActivity, MessengerService::class.java)
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        unbindService(mConnection)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class MessengerHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MyConstants.MSG_FROM_SERVER -> {
                    Log.i(TAG, "receive msg from server: ${msg.data.getString("reply")}")
                }
                else -> super.handleMessage(msg)
            }
        }
    }
}