package com.example.myapplication.chapter_2.aidl

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import com.example.myapplication.aidl.Book
import com.example.myapplication.aidl.IBookManager
import com.example.myapplication.aidl.IOnNewBookArrivedListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 *    @author wangruixiang
 *    @date 2021/4/12 6:01 PM
 */
class BookManagerService : Service() {

    companion object {
        const val TAG = "wangruixiang"
    }

    private val mBookList = CopyOnWriteArrayList<Book>()

    private val mListeners = RemoteCallbackList<IOnNewBookArrivedListener>()

    fun <T : IInterface> RemoteCallbackList<T>.getSize(): Int {
        val n = this.beginBroadcast()
        finishBroadcast()
        return n
    }

    private val mIsServiceDestroyed = AtomicBoolean(false)

    private val mBinder = object : IBookManager.Stub() {
        override fun registerListener(listener: IOnNewBookArrivedListener?) {
            mListeners.register(listener)
            Log.i(TAG, "BookManagerService.registerListener(), size: ${mListeners.getSize()}")
        }

        override fun unregisterListener(listener: IOnNewBookArrivedListener?) {
            mListeners.unregister(listener)
            Log.i(
                TAG,
                "BookManagerService.unregisterListener(), current size: ${mListeners.getSize()}"
            )
        }

        override fun addBook(book: Book?) {
            mBookList.add(book)
        }

        override fun getBookList(): MutableList<Book> {
            Thread.sleep(5000)
            return mBookList
        }

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            val check = checkCallingOrSelfPermission("com.example.myapplication.chapter_2.permission.ACCESS_BOOK_SERVICE")
            if (check == PackageManager.PERMISSION_DENIED) {
                return false
            }

            val packages = packageManager.getPackagesForUid(Binder.getCallingUid())
            if (!packages.isNullOrEmpty()) {
                if (!packages[0].startsWith("com.example.myapplication")) {
                    return false
                }
            }

            return super.onTransact(code, data, reply, flags)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mBookList.add(Book(1, "Android"))
        mBookList.add(Book(2, "iOS"))
        Log.d(
            TAG,
            "BookManagerService.onCreate(), onCreate() in thread ${Thread.currentThread().name}"
        )
        GlobalScope.launch {
            Log.d(
                TAG,
                "BookManagerService.onCreate(), launch in thread ${Thread.currentThread().name}"
            )
            while (!mIsServiceDestroyed.get()) {
                delay(1000)
                val bookId = mBookList.size + 1
                try {
                    addNewBook(Book(bookId, "new book#$bookId"))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
        Log.d(TAG, "BookManagerService.onCreate() end.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        val check = checkCallingOrSelfPermission("com.example.myapplication.chapter_2.permission.ACCESS_BOOK_SERVICE")
        if (check == PackageManager.PERMISSION_DENIED) {
            return null
        }
        return mBinder
    }

    private fun addNewBook(newBook: Book) {
        mBookList.add(newBook)
        val size = mListeners.beginBroadcast()
        Log.d(TAG, "BookManagerService.addNewBook(), notify listeners: $size, book = $newBook")
        for (i in 0 until size) {
            Log.d(TAG, "BookManagerService.addNewBook(), notify listener#$i, book = $newBook")
            mListeners.getBroadcastItem(i).onNewBookArrived(newBook)
        }
        mListeners.finishBroadcast()
    }

    override fun onDestroy() {
        mIsServiceDestroyed.set(true)
        super.onDestroy()
    }
}