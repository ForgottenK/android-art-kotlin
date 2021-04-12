package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "wangruixiang"
        const val DIR_PATH = "/user"
        const val FILE_PATH = "/chapter_2_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()


        }
    }

    private fun persistToFile() {
        Thread(Runnable {
            val user =
                User(1, "hello world", true)
            val dir = File(externalCacheDir?.absolutePath + DIR_PATH)
            if (!dir.exists()) {
                dir.mkdir()
            }
            val cachedFile = File(dir.absolutePath + FILE_PATH)
            var objectOutputStream: ObjectOutputStream? = null
            try {
                objectOutputStream = ObjectOutputStream(FileOutputStream(cachedFile))
                objectOutputStream.writeObject(user)
                Log.d(TAG, "persist user: $user")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                objectOutputStream?.close()
            }
        }).start()
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