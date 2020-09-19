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

    var totalElapsedSeconds: Long = 0L
        internal set

    internal fun setTimeAtSeconds(seconds: Long) {
        base = SystemClock.elapsedRealtime() - (seconds * 1000L)
    }

    internal fun calculateTotalElapsedSeconds(): Long {
        return (SystemClock.elapsedRealtime() - base)/1000L
    }

    var currentState: State = State.EMPTY
        private set(value) {
            field = value
            onStateChangedListener?.onStateChanged(value)
        }

    override fun start() {
        if(currentState != State.IDLE && currentState != State.EMPTY) return

        setTimeAtSeconds(totalElapsedSeconds)
        super.start()

        currentState = State.RUNNING
    }

    fun resume() {
        if(currentState != State.PAUSED) return

        setTimeAtSeconds(totalElapsedSeconds)
        super.start()

        currentState = State.RUNNING
    }

    override fun stop() {
        if(currentState != State.RUNNING && currentState != State.PAUSED) return

        totalElapsedSeconds = calculateTotalElapsedSeconds()
        super.stop()

        currentState = State.IDLE
    }

    fun pause() {
        if(currentState != State.RUNNING) return

        totalElapsedSeconds = calculateTotalElapsedSeconds()
        super.stop()

        currentState = State.PAUSED
    }

    fun clear() {
        if(currentState != State.IDLE) return

        totalElapsedSeconds = 0L
        resetText()

        currentState = State.EMPTY
    }

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
            }
            State.RUNNING -> {
                base = SystemClock.elapsedRealtime() - (seconds * 1000L)
                totalElapsedSeconds = seconds
                setText(seconds)

                super.start()

                currentState = State.RUNNING
            }
        }
    }

    override fun setOnChronometerTickListener(newListener: OnChronometerTickListener?) {
        super.setOnChronometerTickListener {
            if( currentState == State.RUNNING ) {
                totalElapsedSeconds = calculateTotalElapsedSeconds()
                onPausableChronometerTickListener?.onTick(totalElapsedSeconds, currentState)
                newListener?.onChronometerTick(it)
            }
        }
    }

    interface OnStateChangedListener {
        fun onStateChanged(state: State)
    }
    var onStateChangedListener: OnStateChangedListener? = null
    fun setOnStateChangedListener(listener: ((State)->Unit)?) {
        listener?.let {
            setOnStateChangedListener {
                object : OnStateChangedListener {
                    override fun onStateChanged(state: State) {
                        listener.invoke(state)
                    }
                }
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