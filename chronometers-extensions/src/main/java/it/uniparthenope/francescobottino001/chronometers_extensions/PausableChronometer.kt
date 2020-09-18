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

    final override fun start() {
        if(currentState == State.IDLE) {
            base = SystemClock.elapsedRealtime()
            currentState = State.RUNNING
            super.start()
            stateListener?.onStateRunning()
        }
    }

    final fun resume() {
        if(currentState == State.PAUSED) {
            base = SystemClock.elapsedRealtime() + totalElapsedSeconds
            currentState = State.RUNNING
            super.start()
            stateListener?.onStateRunning()
        }
    }

    final override fun stop() {
        if(currentState == State.RUNNING || currentState == State.PAUSED) {
            totalElapsedSeconds = base - SystemClock.elapsedRealtime();
            currentState = State.IDLE
            super.stop()
            resetText()
            stateListener?.onStateIdle()
        }
    }

    final fun pause() {
        if(currentState == State.RUNNING) {
            totalElapsedSeconds = base - SystemClock.elapsedRealtime();
            currentState = State.PAUSED
            super.stop()
            stateListener?.onStatePaused()
        }
    }

    interface StateListener {
        fun onStateIdle()
        fun onStateRunning()
        fun onStatePaused()
    }
    var stateListener: StateListener? = null
}