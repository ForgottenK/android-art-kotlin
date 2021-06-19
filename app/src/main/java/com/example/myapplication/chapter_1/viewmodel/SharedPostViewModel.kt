package com.example.myapplication.chapter_1.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.example.myapplication.chapter_1.model.entity.Post
import com.example.myapplication.chapter_1.model.repository.PostRepository

/**
 *    @author wangruixiang
 *    @date 2021/6/20 1:13 AM
 */
class SharedPostViewModel(private val postRepository: PostRepository) : ViewModel() {
    val allPosts: LiveData<List<Post>> = postRepository.getPosts().asLiveData()

    private val mutableSelectedPost: MutableLiveData<Post> = MutableLiveData()

    @MainThread
    fun setSelectedPost(post: Post) {
        mutableSelectedPost.value = post
    }

    val selectedPost: LiveData<Post> = mutableSelectedPost

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