package com.edu.multithreadasynclearning

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View


class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private lateinit var paint: Paint
    private lateinit var rect: RectF

    var angle: Float = 0.0f
    private var size: Float = 0.0f

    init {
        initCircle(attrs, defStyle)
    }



    private fun initCircle(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CircleView, defStyle, 0
        ).apply {

            size = getDimension(R.styleable.CircleView_size, 40f)*10
            angle = getFloat(R.styleable.CircleView_angle, 0f)
        }


        val circleStrokeWidth = 40


        paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = circleStrokeWidth.toFloat()
            color = Color.RED
        }
        rect = RectF()

        rect.left = paddingLeft.toFloat() + circleStrokeWidth
        rect.top = paddingTop.toFloat() + circleStrokeWidth
        rect.right = size + width.toFloat() - paddingRight - circleStrokeWidth
        rect.bottom = size + height.toFloat() - paddingBottom - circleStrokeWidth
        // rect = RectF(
        //     circleStrokeWidth.toFloat(),
        //     circleStrokeWidth.toFloat(),
        //     size + circleStrokeWidth,
        //     size + circleStrokeWidth
        // )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(rect, START_ANGLE_POINT.toFloat(), angle, false, paint)
    }

    companion object {
        private const val START_ANGLE_POINT = 0
    }

    fun updateAngle(newAngle: Long){
        angle = newAngle.toFloat()
        requestLayout()
    }
}