package com.example.myapplication.chapter_1

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R

/**
 *    @author wangruixiang
 *    @date 2021/6/14 10:41 PM
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        addFragmentIfAbsent(R.id.fragment_post_list, PostListFragment.newInstance())
        addFragmentIfAbsent(R.id.fragment_post_detail, PostDetailFragment.newInstance(null))
    }
}

fun AppCompatActivity.addFragmentIfAbsent(@IdRes fragmentId: Int, fragment: Fragment): Boolean {
    val current = supportFragmentManager.findFragmentById(fragmentId)
    if (current === null) {
        supportFragmentManager.beginTransaction().add(fragmentId, fragment).commit()
        return true
    }
    return false
}