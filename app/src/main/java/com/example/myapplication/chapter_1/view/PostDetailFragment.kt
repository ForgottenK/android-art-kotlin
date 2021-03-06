package com.example.myapplication.chapter_1.view

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.KEY_SELECTED_POST
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.TAG
import com.example.myapplication.chapter_1.model.entity.Post
import com.example.myapplication.chapter_1.viewmodel.SharedPostViewModel
import com.example.myapplication.chapter_1.viewmodel.SharedPostViewModelFactory

/**
 *    @author wangruixiang
 *    @date 2021/6/14 11:10 PM
 */
class PostDetailFragment : Fragment() {
    private lateinit var detailText: TextView
    private lateinit var btnLikePost: Button
    private lateinit var btnGotoDetail: Button

    private val sharedPostViewModel: SharedPostViewModel by activityViewModels {
        SharedPostViewModelFactory(MyApplication.instance!!.postRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.getParcelable<Post>(KEY_SELECTED_POST)?.let {
            sharedPostViewModel.setSelectedPost(it)
            Log.d(TAG, "PostDetailFragment.onViewCreated(), selectedPost = $it")
        }

        detailText = view.findViewById(R.id.tv_fragment_detail_content)
        btnLikePost = view.findViewById(R.id.btn_post_liked)
        btnGotoDetail = view.findViewById(R.id.btn_goto_post_detail)

        btnLikePost.setOnClickListener {
            sharedPostViewModel.onLikeClicked()
        }
        btnLikePost.visibility = View.GONE

        btnGotoDetail.setOnClickListener {
            sharedPostViewModel.gotoDetailActivity(this@PostDetailFragment)
        }
        btnGotoDetail.visibility = View.GONE

        sharedPostViewModel.selectedPost.observe(viewLifecycleOwner) {
            updatePostStatus(it)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "PostDetailFragment.onConfigurationChanged()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        sharedPostViewModel.selectedPost.value?.let { post ->
            Log.d(TAG, "PostDetailFragment.onSaveInstanceState(), selectedPost = $post")
            outState.putParcelable(KEY_SELECTED_POST, post)
        }
    }

    private fun updatePostStatus(post: Post) {
        detailText.text = post.toDisplayString()

        val string = resources.getString(
            if (post.like) R.string.dislike_post
            else R.string.like_post
        )
        btnLikePost.text = string

        btnLikePost.visibility = View.VISIBLE
        btnGotoDetail.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(): PostDetailFragment {
            return PostDetailFragment()
        }
    }
}