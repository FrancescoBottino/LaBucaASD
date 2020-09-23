package it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import it.uniparthenope.francescobottino001.labucaasd.fadeIn as superFadeIn
import it.uniparthenope.francescobottino001.labucaasd.fadeOut as superFadeOut

class Overlay @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(ctx, attrs, defStyleAttr) {

    fun fadeIn(animationDuration: Long) {
        this.superFadeIn(animationDuration)
            .withStartAction {
                this@Overlay.visibility = VISIBLE
            }
            .withEndAction {
                this@Overlay.isClickable = true
            }
    }

    fun fadeOut(animationDuration: Long) {
        this.superFadeOut(animationDuration)
            .withStartAction {
                this@Overlay.isClickable = false
            }
            .withEndAction {
                this@Overlay.visibility = GONE
            }
    }
}