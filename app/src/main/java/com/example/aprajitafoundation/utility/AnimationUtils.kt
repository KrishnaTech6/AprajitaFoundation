package com.example.aprajitafoundation.utility

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation

object AnimationUtils {
    // Fade animation
    fun fadeIn(view: View, duration: Long) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = duration
        view.startAnimation(fadeIn)
        view.visibility = View.VISIBLE
    }

    fun fadeOut(view: View, duration: Long) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = duration
        view.startAnimation(fadeOut)
        view.visibility = View.INVISIBLE
    }

    // Translate (Move) animation
    fun translate(view: View, fromX: Float, toX: Float, fromY: Float, toY: Float, duration: Long) {
        val translate = TranslateAnimation(fromX, toX, fromY, toY)
        translate.duration = duration
        view.startAnimation(translate)
    }

    // Scale animation (Zoom In/Out)
    fun scale(view: View, fromX: Float, toX: Float, fromY: Float, toY: Float, duration: Long) {
        val scale = ScaleAnimation(fromX, toX, fromY, toY)
        scale.duration = duration
        scale.fillAfter = true // Keeps the result of the animation
        view.startAnimation(scale)
    }

    // Rotate animation
    fun rotate(view: View, fromDegrees: Float, toDegrees: Float, duration: Long) {
        val rotate = RotateAnimation(fromDegrees, toDegrees, view.width / 2.0f, view.height / 2.0f)
        rotate.duration = duration
        rotate.fillAfter = true
        view.startAnimation(rotate)
    }

    // Slide in from bottom
    fun slideInFromBottom(view: View, duration: Long = 500) {
        val slideIn = TranslateAnimation(0f, 0f, view.height.toFloat(), 0f)
        slideIn.duration = duration
        view.startAnimation(slideIn)
        view.visibility = View.VISIBLE
    }

    // Slide out to bottom
    fun slideOutToBottom(view: View, duration: Long = 500) {
        val slideOut = TranslateAnimation(0f, 0f, 0f, view.height.toFloat())
        slideOut.duration = duration
        view.startAnimation(slideOut)
        view.visibility = View.INVISIBLE
    }
}