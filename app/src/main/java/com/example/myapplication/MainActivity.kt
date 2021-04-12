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
import com.example.myapplication.aidl.Book
import com.example.myapplication.aidl.IBookManager
import com.example.myapplication.aidl.IOnNewBookArrivedListener
import com.example.myapplication.chapter_2.aidl.BookManagerService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "wangruixiang"
        const val MESSAGE_NEW_BOOK_ARRIVED = 1
    }

    private var mRemoteBookManager: IBookManager? = null

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(
                TAG,
                "MainActivity.onServiceDisconnected(), tname: ${Thread.currentThread().name}"
            )
            bindBookManagerService()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val bookManager = IBookManager.Stub.asInterface(service)
            mRemoteBookManager = bookManager
            GlobalScope.launch {
                try {
                    val list = bookManager.bookList
                    Log.i(
                        TAG,
                        "MainActivity.onServiceConnected(), query book list, list.type: ${list.javaClass.canonicalName}"
                    )
                    Log.i(TAG, "MainActivity.onServiceConnected(), query book list: $list")
                    bookManager.addBook(Book(3, "Android开发艺术探索"))
                    val newList = bookManager.bookList
                    Log.i(TAG, "MainActivity.onServiceConnected(), query book list: $newList")
                    bookManager.registerListener(mNewBookListener)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            mRemoteBookManager?.asBinder()?.linkToDeath(mDeathRecipient, 0)
        }
    }

    private val mNewBookListener = object : IOnNewBookArrivedListener.Stub() {
        override fun onNewBookArrived(newBook: Book?) {
            GlobalScope.launch {
                Thread.sleep(5000)
                mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget()
            }
        }
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_NEW_BOOK_ARRIVED -> {
                    Log.d(TAG, "MainActivity.handleMessage(), receive new book: ${msg.obj}")
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private val mDeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            Log.e(TAG, "MainActivity.binderDied(), tname: ${Thread.currentThread().name}")
            bindBookManagerService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "连接服务中", Snackbar.LENGTH_LONG).show()
            bindBookManagerService()
        }
    }

    private fun bindBookManagerService() {
        val intent = Intent(this@MainActivity, BookManagerService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        mRemoteBookManager?.let {
            if (it.asBinder().isBinderAlive) {
                try {
                    Log.i(TAG, "MainActivity.onDestroy(), unregister listener: $mNewBookListener")
                    it.unregisterListener(mNewBookListener)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            it.asBinder().unlinkToDeath(mDeathRecipient, 0)
            unbindService(mConnection)
        }
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
}