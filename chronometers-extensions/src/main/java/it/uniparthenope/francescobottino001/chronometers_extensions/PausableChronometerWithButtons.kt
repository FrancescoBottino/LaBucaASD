package it.uniparthenope.francescobottino001.chronometers_extensions

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer.StateListener

class PausableChronometerWithButtons @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(ctx).inflate(R.layout.pausable_chronometer_with_buttons, this, true)
    }

    private val playButtonCard: CardView = this.findViewById(R.id.play_btn_card)
    private val playButton: ImageButton = this.findViewById(R.id.play_btn)
    private val stopButtonCard: CardView = this.findViewById(R.id.stop_btn_card)
    private val stopButton: ImageButton = this.findViewById(R.id.stop_btn)
    private val timerCard: CardView = this.findViewById(R.id.timer_card)
    private val timer: PausableChronometer = this.findViewById(R.id.timer)

    private val playButtonColorEnabled: Int
    private val playButtonColorDisabled: Int
    private val stopButtonColorEnabled: Int
    private val stopButtonColorDisabled: Int

    private val playIconDrawable = R.drawable.ic_round_play_arrow_24
    private val pauseIconDrawable = R.drawable.ic_round_pause_24
    private val stopIconDrawable = R.drawable.ic_round_stop_24
    private val clearIconDrawable = R.drawable.ic_round_clear_24

    //Custom attributes
    init {
        val attributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.PausableChronometerWithButtons
        )

        playButtonColorEnabled = attributes.getColor(
            R.styleable.PausableChronometerWithButtons_playButtonEnabledColor,
            ContextCompat.getColor(context, R.color.btn_play_color_enabled)
        )
        playButtonColorDisabled = attributes.getColor(
            R.styleable.PausableChronometerWithButtons_playButtonDisabledColor,
            ContextCompat.getColor(context, R.color.btn_play_color_disabled)
        )
        stopButtonColorEnabled = attributes.getColor(
            R.styleable.PausableChronometerWithButtons_stopButtonEnabledColor,
            ContextCompat.getColor(context, R.color.btn_stop_color_enabled)
        )
        stopButtonColorDisabled = attributes.getColor(
            R.styleable.PausableChronometerWithButtons_stopButtonDisabledColor,
            ContextCompat.getColor(context, R.color.btn_stop_color_disabled)
        )

        playButton.setColorFilter( playButtonColorEnabled )
        stopButton.setColorFilter( stopButtonColorDisabled )

        try {
            val textSize = attributes.getDimensionPixelSizeOrThrow(
                R.styleable.PausableChronometerWithButtons_textSize
            )

            timer.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                textSize.toFloat()
            )
        } catch(ignored: Exception) {}

        timer.setTextColor(
            attributes.getColor(
                R.styleable.PausableChronometerWithButtons_textColor,
                Color.BLACK
            )
        )

        val timerParams: FrameLayout.LayoutParams = timer.layoutParams as FrameLayout.LayoutParams
        try {
            val verticalTimerMargin = attributes.getDimensionPixelSizeOrThrow(
                R.styleable.PausableChronometerWithButtons_chronometerVerticalPadding
            )
            timerParams.topMargin = verticalTimerMargin
            timerParams.bottomMargin = verticalTimerMargin
        } catch(ignored: Exception) {}
        try {
            val horizontalTimerMargin = attributes.getDimensionPixelSizeOrThrow(
                R.styleable.PausableChronometerWithButtons_chronometerHorizontalPadding
            )
            timerParams.marginStart = horizontalTimerMargin
            timerParams.marginEnd = horizontalTimerMargin
        } catch(ignored: Exception) {}

        try {
            val cardsDistance = attributes.getDimensionPixelSizeOrThrow(
                R.styleable.PausableChronometerWithButtons_cardsDistance
            )

            (playButtonCard.layoutParams as LayoutParams).marginEnd = cardsDistance
            (stopButtonCard.layoutParams as LayoutParams).marginStart = cardsDistance
        } catch(ignored: Exception) {}

        try {
            val cardsCornerRadius = attributes.getDimensionOrThrow (
                R.styleable.PausableChronometerWithButtons_cardsCornerRadius
            )

            val radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, cardsCornerRadius, context.resources.displayMetrics)

            playButtonCard.radius = radius
            stopButtonCard.radius = radius
            timerCard.radius = radius
        } catch(ignored: Exception) {}

        try {
            val cardsElevation = attributes.getDimensionOrThrow (
                R.styleable.PausableChronometerWithButtons_cardsElevation
            )

            playButtonCard.cardElevation = cardsElevation
            stopButtonCard.cardElevation = cardsElevation
            timerCard.cardElevation = cardsElevation
        } catch(ignored: Exception) {}

        val bgColor = attributes.getColor(
            R.styleable.PausableChronometerWithButtons_cardsBackgroundColor,
            Color.WHITE
        )

        playButtonCard.setCardBackgroundColor(bgColor)
        stopButtonCard.setCardBackgroundColor(bgColor)
        timerCard.setCardBackgroundColor(bgColor)

        attributes.recycle()
    }

    private var playButtonInternalClickListener: OnClickListener? = null
    var playButtonClickListener: OnClickListener? = null
    private val playButtonWrapperClickListener: OnClickListener? = OnClickListener {
        playButtonInternalClickListener?.onClick(it)
        playButtonClickListener?.onClick(it)
    }

    private var stopButtonInternalClickListener: OnClickListener? = null
    var stopButtonClickListener: OnClickListener? = null
    private val stopButtonWrapperClickListener: OnClickListener? = OnClickListener {
        stopButtonInternalClickListener?.onClick(it)
        stopButtonClickListener?.onClick(it)
    }

    init {
        playButton.setOnClickListener(playButtonWrapperClickListener)
        stopButton.setOnClickListener(stopButtonWrapperClickListener)
    }

    fun setTimerLongClickListener(listener: OnLongClickListener?) {
        timer.setOnLongClickListener(listener)
    }

    private val startAction = OnClickListener{ timer.start() }
    private val stopAction = OnClickListener{ timer.stop() }
    private val pauseAction = OnClickListener{ timer.pause() }
    private val resumeAction = OnClickListener{ timer.resume() }
    private val clearAction = OnClickListener{ timer.clear() }

    private fun setStateEmpty() {
        playButton.setImageDrawable(ContextCompat.getDrawable(context, playIconDrawable))
        playButton.setColorFilter(playButtonColorEnabled)
        playButton.isClickable = true
        playButtonInternalClickListener = resumeAction

        stopButton.setImageDrawable(ContextCompat.getDrawable(context, stopIconDrawable))
        stopButton.setColorFilter(stopButtonColorDisabled)
        stopButton.isClickable = false
        stopButtonInternalClickListener = null
    }
    private fun setStateIdle() {
        playButton.setImageDrawable(ContextCompat.getDrawable(context, playIconDrawable))
        playButton.setColorFilter(playButtonColorEnabled)
        playButton.isClickable = true
        playButtonInternalClickListener = startAction

        stopButton.setImageDrawable(ContextCompat.getDrawable(context, clearIconDrawable))
        stopButton.setColorFilter(stopButtonColorEnabled)
        stopButton.isClickable = true
        stopButtonInternalClickListener = clearAction
    }
    private fun setStateRunning() {
        playButton.setImageDrawable(ContextCompat.getDrawable(context, pauseIconDrawable))
        playButton.setColorFilter(playButtonColorEnabled)
        playButton.isClickable = true
        playButtonInternalClickListener = pauseAction

        stopButton.setImageDrawable(ContextCompat.getDrawable(context, stopIconDrawable))
        stopButton.setColorFilter(stopButtonColorEnabled)
        stopButton.isClickable = true
        stopButtonInternalClickListener = stopAction
    }
    private fun setStatePaused() {
        playButton.setImageDrawable(ContextCompat.getDrawable(context, playIconDrawable))
        playButton.setColorFilter(playButtonColorEnabled)
        playButton.isClickable = true
        playButtonInternalClickListener = resumeAction

        stopButton.setImageDrawable(ContextCompat.getDrawable(context, stopIconDrawable))
        stopButton.setColorFilter(stopButtonColorEnabled)
        stopButton.isClickable = true
        stopButtonInternalClickListener = stopAction
    }

    var stateListener: StateListener? = null

    private val wrapperStateListener: StateListener = object: StateListener {
        override fun onStateIdle() {
            setStateIdle()
            stateListener?.onStateIdle()
        }

        override fun onStateRunning() {
            setStateRunning()
            stateListener?.onStateRunning()
        }

        override fun onStatePaused() {
            setStatePaused()
            stateListener?.onStatePaused()
        }

        override fun onStateEmpty() {
            setStateEmpty()
            stateListener?.onStateEmpty()
        }
    }

    init {
        setStateIdle()
        timer.stateListener = wrapperStateListener
    }
}