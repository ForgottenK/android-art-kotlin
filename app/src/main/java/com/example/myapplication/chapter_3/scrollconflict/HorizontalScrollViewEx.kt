package com.example.myapplication.chapter_3.scrollconflict

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 *    @author wangruixiang
 *    @date 2021/4/21 4:28 PM
 */
class HorizontalScrollViewEx(
    context: Context, attrs: AttributeSet?, defStyle: Int
) : ViewGroup(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    companion object {
        const val TAG = "wangruixiang"
    }

    private val scroller = Scroller(context)
    private val velocityTracker = VelocityTracker.obtain()

    private var childrenSize = 0
    private var childWidth = 0
    private var childIndex = -1

    private var lastX = 0
    private var lastY = 0
    private var lastInterceptX = 0
    private var lastInterceptY = 0

    private var containMove = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) {
            return false
        }

        val x = ev.x.toInt()
        val y = ev.y.toInt()

        val intercepted: Boolean = when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                    true
                } else {
                    false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - lastInterceptX
                val deltaY = y - lastInterceptY
                abs(deltaX) > abs(deltaY)
            }
            else -> false
        }

        Log.d(TAG, "HorizontalScrollViewEx.onInterceptTouchEvent(), intercepted = $intercepted")
        lastX = x
        lastY = y
        lastInterceptX = x
        lastInterceptY = y

        return intercepted
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        velocityTracker.addMovement(event)
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                }
                containMove = false
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - lastX
                scrollBy(-deltaX, 0)
                containMove = true
            }
            MotionEvent.ACTION_UP -> {
                val scrollX = this.scrollX
                val scrollToChildIndex = scrollX / childWidth
                velocityTracker.computeCurrentVelocity(1000)
                val xVelocity = velocityTracker.xVelocity
                childIndex = if (abs(xVelocity) >= 50) {
                    if (xVelocity > 0) childIndex - 1 else childIndex + 1
                } else {
                    (scrollX - childWidth / 2) / childWidth
                }
                childIndex = max(0, min(childIndex, childrenSize - 1))
                val dx = childIndex * childWidth - scrollX
                smoothScrollBy(dx, 0)
                velocityTracker.clear()

                // 处理onClick事件
                if (!containMove) {
                    performClick()
                }
            }
        }

        lastX = x
        lastY = y
        return true
    }

    override fun performClick(): Boolean {
        Log.d(TAG, "HorizontalScrollViewEx.performClick()")
        return super.performClick()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val childCount = this.childCount
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        if (childCount == 0) {
            setMeasuredDimension(0, 0)
        } else {
            val childView = getChildAt(0)
            // 处理 android:width = wrap_content 的情况
            val measuredWidth = if (widthSpecMode == MeasureSpec.AT_MOST) {
                childView.measuredWidth * childCount
            } else {
                widthSpecSize
            }
            // 处理 android:height = wrap_content 的情况
            val measuredHeight = if (heightSpecMode == MeasureSpec.AT_MOST) {
                childView.measuredHeight
            } else {
                heightSpecSize
            }
            setMeasuredDimension(measuredWidth, measuredHeight)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childLeft = 0
        val childCount = this.childCount
        childrenSize = childCount

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView.visibility != View.GONE) {
                val childWidth = childView.measuredWidth
                this.childWidth = childWidth
                childView.layout(childLeft, 0, childLeft + childWidth, childView.measuredHeight)
                childLeft += childWidth
            }
        }
    }

    private fun smoothScrollBy(dx: Int, dy: Int) {
        scroller.startScroll(scrollX, 0, dx, dy, 500)
        invalidate()
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            postInvalidate()
        }
    }

    override fun onDetachedFromWindow() {
        velocityTracker.recycle()
        super.onDetachedFromWindow()
    }
}