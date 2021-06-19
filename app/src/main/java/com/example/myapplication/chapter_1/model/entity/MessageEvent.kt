package com.example.myapplication.chapter_1.model.entity

/**
 *    @author wangruixiang
 *    @date 2021/6/17 3:07 PM
 */
sealed class MessageEvent
class CreatePostMessage(val newPost: Post) : MessageEvent()
class LikePostMessage(val id: Int, val like: Boolean) : MessageEvent()
