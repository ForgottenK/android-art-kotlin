package com.example.myapplication.chapter_1

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R

/**
 *    @author wangruixiang
 *    @date 2021/6/14 10:41 PM
 */
class MainActivity : AppCompatActivity() {

    var selectedPost: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        addFragment(
            R.id.fragment_post_list,
            PostListFragment.newInstance(onPostClickListener = object :
                PostListFragment.OnPostClickListener {
                override fun onPostClicked(post: Post) {
                    selectedPost = post
                    addFragment(R.id.fragment_post_detail, PostDetailFragment.newInstance(post))
                }
            })
        )
        selectedPost = savedInstanceState?.getParcelable(KEY_SELECTED_POST)
        Log.d(TAG, "MainActivity.onCreate, selectedPost = $selectedPost")
        addFragment(R.id.fragment_post_detail, PostDetailFragment.newInstance(selectedPost))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "MainActivity.onSaveInstanceState(), selectedPost = $selectedPost")
        outState.putParcelable(KEY_SELECTED_POST, selectedPost)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "MainActivity.onConfigurationChanged()")
    }

    companion object {
        const val TAG = "wangruixiang"
        const val KEY_SELECTED_POST = "key_selected_post"
    }
}

fun AppCompatActivity.addFragment(@IdRes fragmentId: Int, fragment: Fragment) {
    val current = supportFragmentManager.findFragmentById(fragmentId)
    if (current == null) {
        supportFragmentManager.beginTransaction().add(fragmentId, fragment).commit()
    } else {
        supportFragmentManager.beginTransaction().replace(fragmentId, fragment).commit()
    }
}