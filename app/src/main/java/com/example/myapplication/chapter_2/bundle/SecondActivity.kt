package com.example.myapplication.chapter_2.bundle

import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

/**
 *    @author wangruixiang
 *    @date 2021/4/11 3:58 PM
 */
class SecondActivity : AppCompatActivity() {

    private val userInfo
        get() = findViewById<TextView>(R.id.text_second)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("wangruixiang", "SecondActivity.onCreate(), pid = ${Process.myPid()}")
        setContentView(R.layout.activity_second)
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            val one = async { recoverFromFile() }
            userInfo.text = "${one.await() ?: "failed"}"
        }
    }

    private suspend fun recoverFromFile(): User? {
        var user: User? = null
        withContext(Dispatchers.IO) {
            val cachedFile =
                File(externalCacheDir?.absolutePath + MainActivity.DIR_PATH + MainActivity.FILE_PATH)
            if (cachedFile.exists()) {
                var objectInputStream: ObjectInputStream? = null
                try {
                    objectInputStream = ObjectInputStream(FileInputStream(cachedFile))
                    user = objectInputStream.readObject() as User
                    Log.d("wangruixiang", "user: $user")
//                userInfo.text = "$user"
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    objectInputStream?.close()
                }
            }
        }
        return user
    }
}