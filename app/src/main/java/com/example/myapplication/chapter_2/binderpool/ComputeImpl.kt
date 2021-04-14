package com.example.myapplication.chapter_2.binderpool

import com.example.myapplication.aidl.ICompute

/**
 *    @author wangruixiang
 *    @date 2021/4/14 9:28 PM
 */
class ComputeImpl : ICompute.Stub() {
    override fun add(a: Int, b: Int): Int {
        return a + b
    }
}