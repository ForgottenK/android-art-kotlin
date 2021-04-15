package com.example.myapplication.chapter_3.viewscroll

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 *    @author wangruixiang
 *    @date 2021/4/15 11:38 PM
 */
class MotionView : View {
    companion object {
        const val TAG = "wangruixiang"
    }

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    )

    var lastX: Float = 0f
    var lastY: Float = 0f
//    var transX: Float = 0f
//    var transY: Float = 0f

    override fun performClick(): Boolean {
        Log.d(TAG, "MotionView.performClick()")
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let {
            val x = event.rawX
            val y = event.rawY
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = x
                    lastY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = x - lastX
                    val deltaY = y - lastY
                    Log.d(
                        MotionView::class.java.simpleName,
                        "move, deltaX = $deltaX, deltaY = $deltaY"
                    )
                    // 太麻烦，直接设置 translationX/Y 的值更方便
//                    ObjectAnimator.ofFloat(this, "translationX", transX, transX + deltaX)
//                        .setDuration(1).start()
//                    ObjectAnimator.ofFloat(this, "translationY", transY, transY + deltaY)
//                        .setDuration(1).start()
//                    transX += deltaX
//                    transY += deltaY
                    translationX += deltaX
                    translationY += deltaY
                }
                MotionEvent.ACTION_UP -> performClick()
            }
            lastX = x
            lastY = y

            true
        } ?: super.onTouchEvent(event)
    }
}