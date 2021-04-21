package com.example.myapplication.chapter_3.scrollconflict

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.android.material.snackbar.Snackbar

/**
 *    @author wangruixiang
 *    @date 2021/4/21 5:24 PM
 */
class ScrollConflictActivity : AppCompatActivity() {
    companion object {
        const val TAG = "wangruixiang"
    }

    private var listContainer: HorizontalScrollViewEx? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll_conflict)
        Log.d(TAG, "ScrollConflictActivity.onCreate()")
        initView()
    }

    private fun initView() {
        val inflater = layoutInflater
        listContainer = findViewById(R.id.activity_scroll_conflict_container)
        val displayMetrics = getScreenMetrics()
        val screenWidth = displayMetrics.widthPixels
        for (i in 0..2) {
            val layout: ViewGroup =
                inflater.inflate(R.layout.content_layout, listContainer, false) as ViewGroup
            layout.layoutParams.width = screenWidth
            val textView = layout.findViewById<TextView>(R.id.content_layout_title)
            textView.text = "page ${i + 1}"
            layout.setBackgroundColor(Color.rgb(255 / (i + 1), 255 / (i + 1), 0))
            createList(layout)
            listContainer?.addView(layout)
        }
    }

    private fun createList(layout: ViewGroup) {
        val listView = layout.findViewById<ListView>(R.id.content_layout_list)
        val dataList = arrayListOf<String>()
        for (i in 0 until 50) {
            dataList.add("name $i")
        }
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, R.layout.content_list_item, R.id.content_list_item_name, dataList)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
            Snackbar.make(view, "click item $position", Snackbar.LENGTH_LONG).show()
        }
    }
}

fun AppCompatActivity.getScreenMetrics(): DisplayMetrics {
    val windowManager: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}