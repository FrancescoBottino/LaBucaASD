package it.uniparthenope.francescobottino001.labucaasd

import android.view.View
import android.view.ViewPropertyAnimator


fun View.fadeIn(animationDuration: Long): ViewPropertyAnimator {
    return animate().apply {
        alpha(1f)
        duration = animationDuration
    }
}

fun View.scaleIn(animationDuration: Long): ViewPropertyAnimator {
    return animate().apply {
        scaleX(1f)
        scaleY(1f)
        duration = animationDuration
    }
}

fun View.fadeOut(animationDuration: Long): ViewPropertyAnimator {
    return animate().apply {
        alpha(0f)
        duration = animationDuration
    }
}

fun View.scaleOut(animationDuration: Long): ViewPropertyAnimator {
    return animate().apply {
        scaleX(0f)
        scaleY(0f)
        duration = animationDuration
    }
}