package com.example.myapplication.chapter_4

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

/**
 *    @author wangruixiang
 *    @date 2021/4/21 5:24 PM
 */
class CircleViewActivity : AppCompatActivity() {
    companion object {
        const val TAG = "wangruixiang"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_view)
        Log.d(TAG, "CircleViewActivity.onCreate()")
    }
}