package com.example.myapplication.chapter_4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.myapplication.R
import kotlin.math.min

/**
 *    @author wangruixiang
 *    @date 2021/4/21 6:07 PM
 */
class CircleView(
    context: Context, attrs: AttributeSet?, defStyle: Int
) : View(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var color = Color.RED
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleView)
        color = a.getColor(R.styleable.CircleView_circle_color, Color.RED)
        a.recycle()
        paint.color = color
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val measuredWidth = if (widthSpecMode == MeasureSpec.AT_MOST) 200 else widthSpecSize
        val measuredHeight = if (heightSpecMode == MeasureSpec.AT_MOST) 200 else heightSpecSize
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }

        super.onDraw(canvas)
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        val radius = min(contentWidth, contentHeight) / 2
        canvas.drawCircle(
            (paddingLeft + contentWidth / 2).toFloat(),
            (paddingTop + contentHeight / 2).toFloat(),
            radius.toFloat(),
            paint
        )
    }
}