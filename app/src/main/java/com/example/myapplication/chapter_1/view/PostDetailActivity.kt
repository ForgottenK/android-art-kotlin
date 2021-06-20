package com.example.myapplication.chapter_1.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.KEY_POST
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.TAG
import com.example.myapplication.chapter_1.model.entity.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 *    @author wangruixiang
 *    @date 2021/6/17 12:51 PM
 */
class PostDetailActivity : AppCompatActivity() {

    private lateinit var tvPostDetail: TextView
    private lateinit var btnPostLike: Button
    private lateinit var btnCreatePost: FloatingActionButton

    private lateinit var post: Post
    private val postRepository by lazy { MyApplication.instance!!.postRepository }

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
            post.like = !post.like
            updatePostStatus()
            postRepository.fakeWritePost(post)
        }
        btnPostLike.visibility = View.GONE

        btnCreatePost.setOnClickListener {
            val newPost = Post(MyApplication.userCreatedId, "master", "new post")
            postRepository.fakeWritePost(newPost)
        }
    }

    private fun updatePostStatus() {
        tvPostDetail.text = post.toDisplayString()

        val string = resources.getString(
            if (post.like) R.string.dislike_post
            else R.string.like_post
        )
        btnPostLike.text = string
    }

    private fun initData() {
        intent.getParcelableExtra<Post>(KEY_POST)?.let {
            Log.d(TAG, "PostDetailActivity.initData(), post.hashCode = ${it.hashCode()}")
            post = it
            updatePostStatus()
            btnPostLike.visibility = View.VISIBLE
        }
    }
}