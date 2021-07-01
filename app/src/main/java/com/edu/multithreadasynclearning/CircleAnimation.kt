package com.edu.multithreadasynclearning

import android.view.animation.Animation
import android.view.animation.Transformation


class CircleAnimation(
    private val circleView: CircleView,
    private val newAngle: Int
) : Animation() {
    private val oldAngle: Float = newAngle.toFloat()

    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
        val angle = oldAngle + (newAngle - oldAngle) * interpolatedTime
        circleView.updateAngle(angle)
    }
}