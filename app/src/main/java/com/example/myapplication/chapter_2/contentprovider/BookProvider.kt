package com.example.myapplication.chapter_2.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log

/**
 *    @author wangruixiang
 *    @date 2021/4/13 5:14 PM
 */
class BookProvider : ContentProvider() {
    companion object {
        const val TAG = "wangruixiang"
        private const val AUTHORITY = "com.example.myapplication.chapter_2.book.provider"
        val BOOK_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/book")
        val USER_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/user")
        const val BOOK_URI_CODE = 0
        const val USER_URI_CODE = 1
        val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE)
            sUriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE)
        }
    }

    private var mContext: Context? = null
    private lateinit var mDb: SQLiteDatabase

    override fun onCreate(): Boolean {
        Log.d(TAG, "BookProvider.onCreate(), current thread: ${Thread.currentThread().name}")
        mContext = context
        mDb = DbOpenHelper(mContext).writableDatabase
        initProviderData()
        return true
    }

    private fun initProviderData() {
        mDb.execSQL("delete from ${DbOpenHelper.BOOK_TABLE_NAME}")
        mDb.execSQL("delete from ${DbOpenHelper.USER_TABLE_NAME}")
        mDb.execSQL("insert into book values(3, 'Android');")
        mDb.execSQL("insert into book values(4, 'iOS');")
        mDb.execSQL("insert into book values(5, 'HTML5');")
        mDb.execSQL("insert into user values(1, 'jake', 1);")
        mDb.execSQL("insert into user values(2, 'jasmine', 0);")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "BookProvider.query(), current thread: ${Thread.currentThread().name}")
        val table = getTableName(uri) ?: throw IllegalArgumentException("Unsupported URI: $uri")
        return mDb.query(table, projection, selection, selectionArgs, null, null, sortOrder, null)
    }

    override fun getType(uri: Uri): String? {
        Log.d(TAG, "BookProvider.getType()")
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "BookProvider.insert()")
        val table = getTableName(uri) ?: throw IllegalArgumentException("Unsupported URI: $uri")
        mDb.insert(table, null, values)
        mContext?.contentResolver?.notifyChange(uri, null)
        return uri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "BookProvider.delete()")
        val table = getTableName(uri) ?: throw IllegalArgumentException("Unsupported URI: $uri")
        val count = mDb.delete(table, selection, selectionArgs)
        if (count > 0) {
            mContext?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG, "BookProvider.update()")
        val table = getTableName(uri) ?: throw IllegalArgumentException("Unsupported URI: $uri")
        val row = mDb.update(table, values, selection, selectionArgs)
        if (row > 0) {
            mContext?.contentResolver?.notifyChange(uri, null)
        }
        return row
    }

    private fun getTableName(uri: Uri): String? = when (sUriMatcher.match(uri)) {
        BOOK_URI_CODE -> DbOpenHelper.BOOK_TABLE_NAME
        USER_URI_CODE -> DbOpenHelper.USER_TABLE_NAME
        else -> null
    }
}