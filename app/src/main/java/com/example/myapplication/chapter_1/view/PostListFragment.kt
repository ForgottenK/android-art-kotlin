package com.example.myapplication.chapter_1.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.KEY_COLUMN_COUNT
import com.example.myapplication.chapter_1.model.entity.Constants.Companion.TAG
import com.example.myapplication.chapter_1.model.entity.CreatePostMessage
import com.example.myapplication.chapter_1.model.entity.MessageEvent
import com.example.myapplication.chapter_1.model.entity.Post
import com.example.myapplication.chapter_1.presenter.PostListPresenter
import com.example.myapplication.chapter_1.view.adapter.PostItemRecyclerAdapter
import com.example.myapplication.chapter_1.viewmodel.SharedPostViewModel
import com.example.myapplication.chapter_1.viewmodel.SharedPostViewModelFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *    @author wangruixiang
 *    @date 2021/6/14 11:52 PM
 */
class PostListFragment : Fragment(), IPostListView {
    private lateinit var postList: RecyclerView
    private lateinit var adapter: PostItemRecyclerAdapter

    private val sharedPostViewModel by activityViewModels<SharedPostViewModel> {
        SharedPostViewModelFactory(
            MyApplication.instance!!.postRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postList = view.findViewById(R.id.rv_fragment_post_list)
        postList.layoutManager = let<PostListFragment, RecyclerView.LayoutManager> {
            val columnCount = arguments?.getInt(KEY_COLUMN_COUNT)
            if (columnCount != null && columnCount > 1) {
                GridLayoutManager(context, columnCount)
            } else {
                LinearLayoutManager(context)
            }
        }
        adapter = PostItemRecyclerAdapter(sharedPostViewModel)
        postList.adapter = adapter

        lifecycle.addObserver(PostListPresenter(this))
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun onReceivePostListData(postList: List<Post>) {
        Log.d(TAG, "PostListFragment.onReceivePostListData(), postList.size = ${postList.size}")
        adapter.submitList(postList)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event) {
            is CreatePostMessage -> {
                val posts = adapter.currentList.toMutableList()
                if (posts.isNotEmpty()) {
                    posts.add(0, event.newPost)
                    adapter.submitList(posts)
                    postList.scrollToPosition(0)
                } else {
                    throw IndexOutOfBoundsException("PostListFragment: insert to post list out of bound, current list is empty")
                }
            }
            else -> {
                // ignore
            }
        }
    }

    companion object {
        fun newInstance(columnCount: Int = 1): PostListFragment {
            val args = Bundle()
            args.putInt(KEY_COLUMN_COUNT, columnCount)
            val fragment = PostListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}

interface IPostListView {
    fun onReceivePostListData(postList: List<Post>)
}