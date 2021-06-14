package com.example.myapplication.chapter_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

/**
 *    @author wangruixiang
 *    @date 2021/6/14 10:41 PM
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_post_list, PostListFragment.newInstance())
            .add(R.id.fragment_post_detail, PostDetailFragment.newInstance(null))
            .commit()
    }
}