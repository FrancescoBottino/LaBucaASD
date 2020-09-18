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
        setText(SystemClock.elapsedRealtime() - chronometer.base)
    }

    override fun setOnChronometerTickListener(newListener: OnChronometerTickListener?) {
        super.setOnChronometerTickListener { chronometer ->
            internalTickListener.onChronometerTick(chronometer)
            newListener?.onChronometerTick(chronometer)
        }
    }

    internal fun resetText() {
        setText(0L)
    }

    internal fun setText(seconds: Long) {
        val h = (seconds / 3600000).toInt()
        val m = (seconds - h * 3600000).toInt() / 60000
        val s = (seconds - h * 3600000 - m * 60000).toInt() / 1000
        text = String.format(FORMAT, h, m, s)
    }

    init {
        resetText()
        onChronometerTickListener = internalTickListener
    }
}