package it.uniparthenope.francescobottino001.labucaasd.activities

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.swipe.IDrawerSwipeableViewHolder
import com.mikepenz.fastadapter.swipe.ISwipeable
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometerWithButtons
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import java.util.*

class TimerBinder(
    val timerData: TimerData
) : AbstractItem<TimerBinder.ViewHolder>(), ISwipeable, IDraggable {

    companion object {
        var updateCallback: ((TimerData) -> Unit)? = null
        var editCallback: ((TimerBinder) -> Unit)? = null
        var deleteCallback: ((TimerBinder) -> Unit)? = null

        fun List<TimerData>.toBinderArrayList(): ArrayList<TimerBinder> {
            val bindersList: ArrayList<TimerBinder> = arrayListOf()
            this.forEach {
                bindersList.add(TimerBinder(it))
            }
            return bindersList
        }
    }

    class ViewHolder(private val root: View): FastAdapter.ViewHolder<TimerBinder>(root),
        IDrawerSwipeableViewHolder {

        private val timer: PausableChronometerWithButtons = root.findViewById(R.id.timer)
        private val nameLabel: TextView = root.findViewById(R.id.name_label)
        private val hourlyCostLabel: TextView = root.findViewById(R.id.hourly_cost_label)
        private val totalCostLabel: TextView = root.findViewById(R.id.total_cost_label)

        override val swipeableView: MaterialCardView = root.findViewById(R.id.content)

        private val deleteButton: ImageButton = root.findViewById(R.id.delete_button)
        private val editButton: ImageButton = root.findViewById(R.id.edit_button)

        fun updateCostText(seconds: Long, hourlyCost: Double) {
            val totalCost = (hourlyCost / 3600) * seconds

            totalCostLabel.text = String.format(
                Locale.ITALIAN,
                root.context.resources.getString(R.string.total_cost_label),
                totalCost
            )
        }

        override fun bindView(item: TimerBinder, payloads: List<Any>) {
            nameLabel.text = item.timerData.name

            hourlyCostLabel.text = String.format(
                Locale.ITALIAN,
                root.context.resources.getString(R.string.hourly_cost_label),
                item.timerData.hourlyCost
            )

            updateCostText((item.timerData.elapsedSeconds?:0L), item.timerData.hourlyCost)

            (item.timerData.state?: PausableChronometer.State.EMPTY).let { state ->
                when(state) {
                    PausableChronometer.State.RUNNING -> {
                        item.timerData.savedAt?.let { calendar ->
                            val now = Calendar.getInstance().timeInMillis
                            val then = calendar.timeInMillis
                            val seconds = (then - now) / 1000L

                            timer.setChronometerState(state, seconds)
                        } ?: timer.setChronometerState(state, (item.timerData.elapsedSeconds ?: 0L))
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

                updateCallback?.invoke( item.timerData )

                updateCostText(timer.timer.totalElapsedSeconds, item.timerData.hourlyCost)
            }

            timer.setTimerTickListener { seconds, _ ->
                updateCostText(seconds, item.timerData.hourlyCost)
            }

            deleteButton.setOnClickListener {
                deleteCallback?.invoke(item)
            }

            editButton.setOnClickListener {
                editCallback?.invoke(item)
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

    override val layoutRes: Int
        get() = R.layout.timers_list_item
    override val type: Int
        get() = R.id.timer_item

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
    override val isSwipeable: Boolean = true
    override val isDraggable: Boolean = true
}