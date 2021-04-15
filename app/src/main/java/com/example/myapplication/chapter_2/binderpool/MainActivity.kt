package com.example.myapplication.chapter_2.binderpool

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.aidl.ICompute
import com.example.myapplication.aidl.ISecurityCenter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "wangruixiang"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Binder 连接池测试中", Snackbar.LENGTH_LONG).show()
            thread {
                doWork()
            }
        }
    }

    private fun doWork() {
        val binderPool = BinderPool.getInstance(this)
        val securityBinder = binderPool.queryBinder(BinderPoolService.BINDER_SECURITY_CENTER)
        val iSecurityCenter = ISecurityCenter.Stub.asInterface(securityBinder)
        Log.d(TAG, "MainActivity.doWork(), visit iSecurityCenter")
        try {
            val msg = "helloworld-安卓"
            Log.d(TAG, "MainActivity.doWork(), content: $msg")
            val password = iSecurityCenter.encrypt(msg)
            Log.d(TAG, "MainActivity.doWork(), password: $password")
            Log.d(
                TAG,
                "MainActivity.doWork(), decrypt password: ${iSecurityCenter.decrypt(password)}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val computeBinder = binderPool.queryBinder(BinderPoolService.BINDER_COMPUTE)
        val iCompute = ICompute.Stub.asInterface(computeBinder)
        Log.d(TAG, "MainActivity.doWork(), visit iCompute")
        try {
            Log.d(TAG, "MainActivity.doWork(), 3 + 5 = ${iCompute.add(3, 5)}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
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