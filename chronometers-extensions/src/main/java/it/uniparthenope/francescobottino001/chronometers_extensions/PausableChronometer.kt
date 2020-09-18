package it.uniparthenope.francescobottino001.chronometers_extensions

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet

open class PausableChronometer @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ChronometerWithHours(ctx, attrs, defStyleAttr) {

    enum class State {
        EMPTY, IDLE, RUNNING, PAUSED
    }

    var currentState: State = State.EMPTY
        private set
    var totalElapsedSeconds: Long = 0L
        private set

    override fun start() {
        if(currentState != State.IDLE && currentState != State.EMPTY) return

        base = SystemClock.elapsedRealtime() + (totalElapsedSeconds * 1000L)
        super.start()

        currentState = State.RUNNING
        stateListener?.onStateRunning()
    }

    fun resume() {
        if(currentState != State.PAUSED) return

        base = SystemClock.elapsedRealtime() + (totalElapsedSeconds * 1000L)
        super.start()

        currentState = State.RUNNING
        stateListener?.onStateRunning()
    }

    override fun stop() {
        if(currentState != State.RUNNING && currentState != State.PAUSED) return

        totalElapsedSeconds = (base - SystemClock.elapsedRealtime()) / 1000L
        super.stop()

        currentState = State.IDLE
        stateListener?.onStateIdle()
    }

    fun pause() {
        if(currentState != State.RUNNING) return

        totalElapsedSeconds = (base - SystemClock.elapsedRealtime()) / 1000L
        super.stop()

        currentState = State.PAUSED
        stateListener?.onStatePaused()
    }

    fun clear() {
        if(currentState != State.IDLE) return

        totalElapsedSeconds = 0L
        resetText()

        currentState = State.EMPTY
        stateListener?.onStateEmpty()
    }

    interface StateListener {
        fun onStateIdle()
        fun onStateRunning()
        fun onStatePaused()
        fun onStateEmpty()
    }
    var stateListener: StateListener? = null

    fun setChronometerState(state: State, seconds: Long = 0L) {
        when(state) {
            State.EMPTY, State.IDLE, State.PAUSED -> {
                super.stop()

                if( state == State.EMPTY ) {
                    totalElapsedSeconds = 0L
                    setText(0L)
                } else {
                    totalElapsedSeconds = seconds
                    setText(seconds)
                }

                currentState = state
                when (state) {
                    State.IDLE -> stateListener?.onStateIdle()
                    State.PAUSED -> stateListener?.onStatePaused()
                    State.EMPTY -> stateListener?.onStateEmpty()
                    else -> {}
                }
            }
            State.RUNNING -> {
                totalElapsedSeconds = seconds
                base = SystemClock.elapsedRealtime() - (totalElapsedSeconds * 1000L)

                super.start()

                currentState = State.RUNNING
                stateListener?.onStateRunning()
            }
        }
    }

    override fun setOnChronometerTickListener(newListener: OnChronometerTickListener?) {
        super.setOnChronometerTickListener {
            if( currentState == State.RUNNING ) {
                totalElapsedSeconds++
                onPausableChronometerTickListener?.onTick(totalElapsedSeconds, currentState)
                newListener?.onChronometerTick(it)
            }
        }
    }

    interface OnPausableChronometerTickListener {
        fun onTick(elapsedSeconds: Long, currentState: State)
    }

    private var onPausableChronometerTickListener: OnPausableChronometerTickListener? = null
    fun setOnPausableChronometerTickListener(listener: ((Long, State)->Unit)?) {
        listener?.let {
            setOnPausableChronometerTickListener(
                object: OnPausableChronometerTickListener {
                    override fun onTick(elapsedSeconds: Long, currentState: State) {
                        listener.invoke(elapsedSeconds, currentState)
                    }
                }
            )
        }
    }
    fun setOnPausableChronometerTickListener(listener: OnPausableChronometerTickListener?) {
        onPausableChronometerTickListener = listener
    }
}