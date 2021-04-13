package com.example.myapplication.chapter_2.contentprovider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 *    @author wangruixiang
 *    @date 2021/4/13 5:45 PM
 */
const val DB_NAME = "book_provider.db"
const val DB_VERSION = 1

class DbOpenHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val BOOK_TABLE_NAME = "book"
        const val USER_TABLE_NAME = "user"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $BOOK_TABLE_NAME (_id INTEGER PRIMARY KEY, name TEXT)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS $USER_TABLE_NAME (_id INTEGER PRIMARY KEY, name TEXT, sex INT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("ignored")
    }

}