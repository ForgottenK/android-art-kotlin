package com.example.myapplication.chapter_1.presenter

import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.MyApplication
import com.example.myapplication.chapter_1.view.IPostListView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

/**
 *    @author wangruixiang
 *    @date 2021/6/15 8:36 PM
 */
class PostListPresenter(private val iPostListView: IPostListView) : LifecycleObserver {

    private lateinit var requestPostListJob: Job

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun requestPostListData(owner: LifecycleOwner) {
        requestPostListJob = MyApplication.instance?.postRepository?.getPosts()!!
            .onEach { iPostListView.onReceivePostListData(it) }
            .onCompletion { cause -> val success = cause == null }
            .catch { cause -> Log.e(TAG, "Exception: $cause") }
            .launchIn(owner.lifecycleScope)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Log.d(TAG, "PostListPresenter.onDestroy(), received lifecycle ON_DESTROY event")
        if (requestPostListJob.isActive) {
            requestPostListJob.cancel()
        }
    }

    companion object {
        const val TAG = "wangruixiang"
    }
}