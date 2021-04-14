package com.example.myapplication.chapter_2.binderpool

import com.example.myapplication.aidl.ISecurityCenter

/**
 *    @author wangruixiang
 *    @date 2021/4/14 9:15 PM
 */
class SecurityCenterImpl : ISecurityCenter.Stub() {

    companion object {
        const val SECRET_CODE: Char = '^'
    }

    override fun encrypt(content: String?): String {
        val chars: CharArray = content?.toCharArray() ?: charArrayOf()
        for (i in chars.indices) {
            chars[i] = (chars[i].toInt() xor SECRET_CODE.toInt()).toChar()
        }
        return String(chars)
    }

    override fun decrypt(password: String?): String {
        return encrypt(password)
    }
}