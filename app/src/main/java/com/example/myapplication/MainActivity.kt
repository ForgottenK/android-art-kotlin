package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.chapter_11.LocalIntentService
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
            Snackbar.make(view, "JobIntentService 测试中", Snackbar.LENGTH_LONG).show()
            val service = Intent(this@MainActivity, LocalIntentService::class.java)
            service.putExtra("task_action", "com.wrx.action.TASK1")
            startService(service)
            service.putExtra("task_action", "com.wrx.action.TASK2")
            startService(service)
            service.putExtra("task_action", "com.wrx.action.TASK3")
            startService(service)
            service.putExtra("task_action", "com.wrx.action.TASK4")
            LocalIntentService.enqueueWork(this@MainActivity, service)
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