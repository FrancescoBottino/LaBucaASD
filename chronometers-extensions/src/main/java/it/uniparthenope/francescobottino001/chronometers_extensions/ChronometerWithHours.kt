package it.uniparthenope.francescobottino001.chronometers_extensions

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.widget.Chronometer

open class ChronometerWithHours @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chronometer(ctx, attrs, defStyleAttr) {

    companion object {
        private const val FORMAT: String = "%02d:%02d:%02d"
    }

    private val internalTickListener = OnChronometerTickListener { chronometer ->
        val time: Long = SystemClock.elapsedRealtime() - chronometer.base
        val h = (time / 3600000).toInt()
        val m = (time - h * 3600000).toInt() / 60000
        val s = (time - h * 3600000 - m * 60000).toInt() / 1000
        text = String.format(FORMAT, h, m, s)
    }

    override fun setOnChronometerTickListener(newListener: OnChronometerTickListener?) {
        super.setOnChronometerTickListener { chronometer ->
            internalTickListener.onChronometerTick(chronometer)
            newListener?.onChronometerTick(chronometer)
        }
    }

    fun resetText() {
        text = String.format(FORMAT, 0, 0, 0)
    }

    init {
        resetText()
        onChronometerTickListener = internalTickListener
    }
}