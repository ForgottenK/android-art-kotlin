package com.example.myapplication

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.aidl.Book
import com.example.myapplication.chapter_2.contentprovider.BookProvider
import com.example.myapplication.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "wangruixiang"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "查询数据中", Snackbar.LENGTH_LONG).show()
            addBook()
            queryBook()
            queryUser()
        }
    }

    private fun queryUser() {
        val userUri = BookProvider.USER_CONTENT_URI
        val userCursor =
            contentResolver.query(userUri, arrayOf("_id", "name", "sex"), null, null, null)
        while (userCursor != null && userCursor.moveToNext()) {
            val id = userCursor.getInt(0)
            val name = userCursor.getString(1)
            val isMale = userCursor.getInt(2) == 1
            Log.d(TAG, "query user: ${User(id, name, isMale)}")
        }
        userCursor?.close()
    }

    private fun queryBook() {
        val bookUri = BookProvider.BOOK_CONTENT_URI
        contentResolver.query(bookUri, arrayOf("_id", "name"), null, null, null).use {
            if (it != null) {
                while (it.moveToNext()) {
                    val id = it.getInt(0)
                    val name = it.getString(1)
                    Log.d(TAG, "query book: ${Book(id, name)}")
                }
            }
        }
    }

    private fun addBook() {
        val bookUri = BookProvider.BOOK_CONTENT_URI
        val values = ContentValues().also { it.put("_id", 6); it.put("name", "程序设计的艺术") }
        contentResolver.insert(bookUri, values)
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