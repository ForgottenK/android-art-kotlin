package com.example.myapplication.chapter_1.view

import android.content.Intent
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
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.KEY_POST
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.KEY_SELECTED_POST
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.TAG
import com.example.myapplication.chapter_1.model.entity.LikePostMessage
import com.example.myapplication.chapter_1.model.entity.MessageEvent
import com.example.myapplication.chapter_1.model.entity.Post
import com.example.myapplication.chapter_1.viewmodel.SharedPostViewModel
import com.example.myapplication.chapter_1.viewmodel.SharedPostViewModelFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *    @author wangruixiang
 *    @date 2021/6/14 11:10 PM
 */
class PostDetailFragment : Fragment() {
    private lateinit var detailText: TextView
    private lateinit var btnLikePost: Button
    private lateinit var btnGotoDetail: Button

    private val postViewModel: SharedPostViewModel by activityViewModels {
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
            postViewModel.setSelectedPost(it)
            Log.d(TAG, "PostDetailFragment.onViewCreated(), selectedPost = $it")
        }

        detailText = view.findViewById(R.id.tv_fragment_detail_content)
        btnLikePost = view.findViewById(R.id.btn_post_liked)
        btnGotoDetail = view.findViewById(R.id.btn_goto_post_detail)

        btnLikePost.setOnClickListener {
            postViewModel.selectedPost.value?.let {
                it.like = !it.like
                postViewModel.setSelectedPost(it)
            }
        }
        btnLikePost.visibility = View.GONE

        btnGotoDetail.setOnClickListener {
            val post = postViewModel.selectedPost.value!!
            val intent = Intent(activity, PostDetailActivity::class.java)
            Log.d(TAG, "PostDetailFragment.onClick(), post.hashCode = ${post.hashCode()}")
            intent.putExtra(KEY_POST, post)
            startActivity(intent)
        }
        btnGotoDetail.visibility = View.GONE

        EventBus.getDefault().register(this)
        postViewModel.selectedPost.observe(viewLifecycleOwner) {
            updatePostStatus(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event) {
            is LikePostMessage -> {
                postViewModel.selectedPost.value?.let {
                    if (it.id == event.id) {
                        it.like = event.like
                        postViewModel.setSelectedPost(it)
                    }
                }
            }
            else -> {
                // ignore
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "PostDetailFragment.onConfigurationChanged()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val post = postViewModel.selectedPost.value!!
        Log.d(TAG, "PostDetailFragment.onSaveInstanceState(), selectedPost = $post")
        outState.putParcelable(KEY_SELECTED_POST, post)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
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