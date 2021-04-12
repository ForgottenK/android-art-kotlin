package com.example.myapplication.model

import java.io.Serializable

/**
 *    @author wangruixiang
 *    @date 2021/4/11 4:43 PM
 */
data class User(val id: Int, val desc: String, val isMale: Boolean) : Serializable
