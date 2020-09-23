package it.uniparthenope.francescobottino001.labucaasd

import android.view.View
import android.view.ViewPropertyAnimator


fun View.fadeIn(animationDuration: Long): ViewPropertyAnimator {
    return animate().apply {
        alpha(1f)
        duration = animationDuration
    }
}

fun View.fadeOut(animationDuration: Long): ViewPropertyAnimator {
    return animate().apply {
        alpha(0.0f)
        duration = animationDuration
    }
}