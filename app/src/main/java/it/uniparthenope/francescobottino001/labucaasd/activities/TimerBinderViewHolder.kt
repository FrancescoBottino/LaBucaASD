package it.uniparthenope.francescobottino001.labucaasd.activities

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.material.card.MaterialCardView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.swipe.IDrawerSwipeableViewHolder
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometerWithButtons
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.getIfNotNull
import java.util.*

class TimerBinderViewHolder(private val root: View): FastAdapter.ViewHolder<TimerBinder>(root),
    IDrawerSwipeableViewHolder {

    private val timer: PausableChronometerWithButtons = root.findViewById(R.id.timer)
    private val nameLabel: TextView = root.findViewById(R.id.name_label)
    private val hourlyCostLabel: TextView = root.findViewById(R.id.hourly_cost_label)
    private val totalCostLabel: TextView = root.findViewById(R.id.total_cost_label)

    override val swipeableView: MaterialCardView = root.findViewById(R.id.content)

    private val deleteButton: ImageButton = root.findViewById(R.id.delete_button)
    private val editButton: ImageButton = root.findViewById(R.id.edit_button)

    private fun formatCostWithString(cost: Double, @StringRes stringResource: Int): String {
        return String.format(
            Locale.ITALIAN,
            root.context.resources.getString(stringResource),
            cost
        )
    }

    private fun updateCostText(item: TimerBinder, seconds: Long) {
        totalCostLabel.text = formatCostWithString(
            item.timerData.getTotalCost(seconds), R.string.total_cost_label
        )
    }

    override fun bindView(item: TimerBinder, payloads: List<Any>) {
        nameLabel.text = item.timerData.name

        hourlyCostLabel.text = formatCostWithString(
            item.timerData.hourlyCost, R.string.hourly_cost_label
        )

        updateCostText(item, item.timerData.elapsedSeconds?:0L)

        (item.timerData.state ?: PausableChronometer.State.EMPTY).let { state ->
            when(state) {
                PausableChronometer.State.RUNNING -> {
                    val totalSeconds = getIfNotNull(
                        item.timerData.savedAt,
                        item.timerData.elapsedSeconds
                    ) { savedAt, elapsedSeconds ->
                        val now = Calendar.getInstance().timeInMillis
                        val then = savedAt.timeInMillis

                        val secondsPassed = (now - then) / 1000L

                        elapsedSeconds + secondsPassed
                    }

                    timer.setChronometerState(state, totalSeconds?:0L)
                }
                PausableChronometer.State.EMPTY,
                PausableChronometer.State.IDLE,
                PausableChronometer.State.PAUSED -> {
                    (item.timerData.elapsedSeconds ?: 0L).let { seconds ->
                        timer.setChronometerState(state, seconds)
                    }
                }
            }
        }

        timer.setOnStateChangedListener { state ->
            item.timerData.state = state
            item.timerData.elapsedSeconds = timer.timer.totalElapsedSeconds
            item.timerData.savedAt = Calendar.getInstance()

            TimerBinder.updateCallback?.invoke( item.timerData )
            updateCostText(item, timer.timer.totalElapsedSeconds)
        }

        timer.setTimerTickListener { seconds, _ ->
            updateCostText(item , seconds)
        }

        timer.setOnLongClickListener {
            TimerBinder.editTimerCallback?.let {
                it.invoke(item, timer.timer)
                TimerBinder.updateCallback?.invoke( item.timerData )
                return@setOnLongClickListener true
            } ?: return@setOnLongClickListener false
        }

        deleteButton.setOnClickListener {
            TimerBinder.deleteCallback?.invoke(item)
        }

        editButton.setOnClickListener {
            TimerBinder.editCallback?.invoke(item)
        }
    }

    override fun unbindView(item: TimerBinder) {
        nameLabel.text = ""
        hourlyCostLabel.text = ""
        totalCostLabel.text = ""

        timer.setOnStateChangedListener(null)
        timer.setTimerTickListener(null)
        timer.setChronometerState(PausableChronometer.State.EMPTY, 0L)
        deleteButton.setOnClickListener(null)
        editButton.setOnClickListener(null)
    }
}