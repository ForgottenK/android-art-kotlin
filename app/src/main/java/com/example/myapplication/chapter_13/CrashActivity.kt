package com.example.myapplication.chapter_13

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.myapplication.R

/**
 *    @author wangruixiang
 *    @date 2021/4/27 4:40 PM
 */
class CrashActivity : Activity(), View.OnClickListener {

    private var mButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        initView()
    }

    private fun initView() {
        mButton = findViewById(R.id.activity_crash_button)
        mButton?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v === mButton) {
            // 在这里模拟异常抛出情况，人为抛出一个运行时异常
            throw RuntimeException("自定义异常：这是自己抛出的异常")
        }
    }
}