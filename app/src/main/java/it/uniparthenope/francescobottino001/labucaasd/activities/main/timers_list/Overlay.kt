package it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list

import android.content.Context
import android.util.AttributeSet
import android.view.View

class Overlay @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(ctx, attrs, defStyleAttr) {

    fun fadeIn(animationDuration: Long) {
        animate().apply {
            alpha(1f)
            duration = animationDuration
            withStartAction {
                this@Overlay.visibility = VISIBLE
            }
            withEndAction {
                this@Overlay.isClickable = true
            }
        }
    }

    fun fadeOut(animationDuration: Long) {
        animate().apply {
            alpha(0.0f)
            duration = animationDuration
            withEndAction {
                this@Overlay.visibility = GONE
            }
            withStartAction {
                this@Overlay.isClickable = false
            }
        }
    }
}