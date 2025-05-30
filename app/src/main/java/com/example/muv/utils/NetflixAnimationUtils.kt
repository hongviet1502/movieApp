package com.example.muv.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.AccelerateDecelerateInterpolator

object NetflixAnimationUtils {

    /**
     * Scale animation for movie items
     */
    fun scaleView(view: View, scale: Float, duration: Long = NetflixConstants.ANIMATION_DURATION_MEDIUM) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, scale)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, scale)

        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            this.duration = duration
            interpolator = (if (scale > 1f) OvershootInterpolator() else AccelerateDecelerateInterpolator()) as TimeInterpolator?
            start()
        }
    }

    /**
     * Fade in animation
     */
    fun fadeIn(view: View, duration: Long = NetflixConstants.ANIMATION_DURATION_MEDIUM) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator() as TimeInterpolator?)
            .start()
    }

    /**
     * Slide up animation
     */
    fun slideUp(view: View, duration: Long = NetflixConstants.ANIMATION_DURATION_MEDIUM) {
        view.translationY = view.height.toFloat()
        view.animate()
            .translationY(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator() as TimeInterpolator?)
            .start()
    }
}