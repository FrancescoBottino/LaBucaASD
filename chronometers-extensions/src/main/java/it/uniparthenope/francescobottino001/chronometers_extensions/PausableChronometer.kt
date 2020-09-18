package it.uniparthenope.francescobottino001.chronometers_extensions

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet

open class PausableChronometer @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ChronometerWithHours(ctx, attrs, defStyleAttr) {

    enum class State {
        IDLE, RUNNING, PAUSED
    }

    var currentState: State = State.IDLE
        private set
    var totalElapsedSeconds: Long = 0L
        private set

    override fun start() {
        if(currentState != State.IDLE) return

        base = SystemClock.elapsedRealtime() + totalElapsedSeconds
        super.start()

        currentState = State.RUNNING
        stateListener?.onStateRunning()
    }

    fun resume() {
        if(currentState != State.PAUSED) return

        base = SystemClock.elapsedRealtime() + totalElapsedSeconds
        super.start()

        currentState = State.RUNNING
        stateListener?.onStateRunning()
    }

    override fun stop() {
        if(currentState != State.RUNNING && currentState != State.PAUSED) return

        totalElapsedSeconds = base - SystemClock.elapsedRealtime()
        super.stop()

        currentState = State.IDLE
        stateListener?.onStateIdle()
    }

    fun pause() {
        if(currentState != State.RUNNING) return

        totalElapsedSeconds = base - SystemClock.elapsedRealtime()
        super.stop()

        currentState = State.PAUSED
        stateListener?.onStatePaused()
    }

    fun clear() {
        if(currentState != State.IDLE) return

        totalElapsedSeconds = 0L
        resetText()
    }

    interface StateListener {
        fun onStateIdle()
        fun onStateRunning()
        fun onStatePaused()
    }
    var stateListener: StateListener? = null
}