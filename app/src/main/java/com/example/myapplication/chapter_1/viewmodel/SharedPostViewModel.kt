package com.example.myapplication.chapter_1.viewmodel

import android.content.Intent
import android.util.Log
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.myapplication.chapter_1.model.entity.Constants
import com.example.myapplication.chapter_1.model.entity.Post
import com.example.myapplication.chapter_1.model.repository.PostRepository
import com.example.myapplication.chapter_1.view.PostDetailActivity

/**
 *    @author wangruixiang
 *    @date 2021/6/20 1:13 AM
 */
class SharedPostViewModel(private val postRepository: PostRepository) : ViewModel() {
    private val repositoryPosts: LiveData<List<Post>> = postRepository.getPosts().asLiveData()
    val allPosts = MediatorLiveData<List<Post>>()

    private val mutableNeedScroll = MutableLiveData<Boolean>()
    val needScroll: LiveData<Boolean> = mutableNeedScroll

    private val mutableSelectedPost: MutableLiveData<Post> = MutableLiveData()
    val selectedPost: MediatorLiveData<Post> = MediatorLiveData<Post>()

    init {
        initPostList()
        initSelectedPost()
    }

    private fun initPostList() {
        allPosts.addSource(repositoryPosts) {
            allPosts.value = repositoryPosts.value
        }
        allPosts.addSource(postRepository.fakeWritePosts) { fakeWriteList ->
            var needScroll = false
            val newList = mutableListOf<Post>()
            allPosts.value?.let {
                newList.addAll(it)
            }
            for (fakeWritePost in fakeWriteList) {
                val existPost = newList.find { fakeWritePost.id == it.id }
                if (existPost == null) {
                    newList.add(0, fakeWritePost)
                    needScroll = true
                } else {
                    existPost.like = fakeWritePost.like
                }
            }
            allPosts.value = newList
            mutableNeedScroll.value = needScroll
        }
    }

    private fun initSelectedPost() {
        // prefer fake write post, if null, take mutableSelectedPost.value
        selectedPost.addSource(postRepository.fakeWritePosts) { fakeWriteList ->
            val currentId = mutableSelectedPost.value?.id
            fakeWriteList.lastOrNull { currentId == it.id }?.let {
                selectedPost.value = it
            }
        }
        selectedPost.addSource(mutableSelectedPost) { currentPost ->
            val currentId = currentPost.id
            val fakeWriteList = postRepository.fakeWritePosts.value
            val fakeWritePost = fakeWriteList?.lastOrNull { currentId == it.id }
            selectedPost.value = fakeWritePost ?: currentPost
        }
    }

    @MainThread
    fun setSelectedPost(post: Post) {
        mutableSelectedPost.value = post
    }

    @MainThread
    fun onLikeClicked() {
        selectedPost.value?.let {
            it.like = !it.like
            postRepository.fakeWritePost(it)
        }
    }

    @MainThread
    fun gotoDetailActivity(fragment: Fragment) {
        val post = selectedPost.value!!
        val intent = Intent(fragment.activity, PostDetailActivity::class.java)
        Log.d(
            Constants.TAG,
            "SharedPostViewModel.gotoDetailActivity(), post.hashCode = ${post.hashCode()}"
        )
        intent.putExtra(Constants.KEY_POST, post)
        fragment.startActivity(intent)
    }
}

class SharedPostViewModelFactory(private val postRepository: PostRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedPostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedPostViewModel(postRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}