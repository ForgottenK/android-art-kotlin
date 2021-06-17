package com.example.myapplication.chapter_1.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.chapter_1.model.entity.CreatePostMessage
import com.example.myapplication.chapter_1.model.entity.Post
import com.example.myapplication.chapter_1.model.entity.USER_CREATED_ID
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 *    @author wangruixiang
 *    @date 2021/6/17 12:51 PM
 */
class PostDetailActivity : AppCompatActivity() {

    private lateinit var tvPostDetail: TextView
    private lateinit var btnPostLike: Button
    private lateinit var btnCreatePost: FloatingActionButton

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        initView()
        initData()
    }

    private fun initView() {
        tvPostDetail = findViewById(R.id.tv_post_detail)
        btnPostLike = findViewById(R.id.btn_post_liked)
        btnCreatePost = findViewById(R.id.btn_create_post)

        btnPostLike.setOnClickListener {
            // TODO: 2021/6/17 false write post like state
        }
        btnPostLike.visibility = View.GONE

        btnCreatePost.setOnClickListener {
            val newPost = Post(USER_CREATED_ID, "master", "new post", Date())
            EventBus.getDefault().post(CreatePostMessage(newPost))
        }
    }

    private fun initData() {
        intent.getParcelableExtra<Post>(KEY_POST)?.let {
            post = it
            tvPostDetail.text = post.toString()
            // TODO: 2021/6/17 set like status
            btnPostLike.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TAG = "wangruixiang"
        const val KEY_POST = "key_post"
    }


}