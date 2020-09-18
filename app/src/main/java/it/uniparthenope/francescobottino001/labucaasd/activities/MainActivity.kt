package it.uniparthenope.francescobottino001.labucaasd.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.itemanimators.AlphaInAnimator
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometerWithButtons
import it.uniparthenope.francescobottino001.labucaasd.BasicActivity
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BasicActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var adapter: FastItemAdapter<GenericItem>
    private lateinit var timersList: ArrayList<TimerData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FastItemAdapter()

        timers_list.layoutManager = LinearLayoutManager(this)
        timers_list.itemAnimator = AlphaInAnimator()
        timers_list.adapter = adapter

        TimerBinder.saveCallback = viewModel::updateTimer
        viewModel.getListaTimer {
            timersList = ArrayList(it)
            adapter.set(timersList.toBinderArrayList(::TimerBinder))
        }

        fab.setOnClickListener {
            //TODO DIALOG NEW TIMER
            newTimer()
        }
    }

    fun newTimer() {
        val ordinal = timersList.size + 1
        val newTimer = TimerData("PlayStation", 20.0, ordinal.toLong())
        timersList.add(newTimer)

        adapter.itemAdapter.add(TimerBinder(newTimer))

        viewModel.addTimer(newTimer)
    }
}

class TimerBinder(
    val timerData: TimerData
) : AbstractItem<TimerBinder.ViewHolder>() {

    companion object {
        var saveCallback: ((TimerData) -> Unit)? = null
    }

    class ViewHolder(private val root: View): FastAdapter.ViewHolder<TimerBinder>(root) {
        private val timer: PausableChronometerWithButtons = root.findViewById(R.id.timer)
        private val nameLabel: TextView = root.findViewById(R.id.name_label)
        private val hourlyCostLabel: TextView = root.findViewById(R.id.hourly_cost_label)
        private val totalCostLabel: TextView = root.findViewById(R.id.total_cost_label)

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

            (item.timerData.state?:PausableChronometer.State.EMPTY).let { state ->
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
                val timerData = item.timerData

                timerData.state = state
                timerData.elapsedSeconds = timer.timer.totalElapsedSeconds
                timerData.savedAt = Calendar.getInstance()

                saveCallback?.invoke( timerData )

                updateCostText(timer.timer.totalElapsedSeconds, item.timerData.hourlyCost)
            }

            timer.setTimerTickListener { seconds, state ->
                updateCostText(seconds, item.timerData.hourlyCost)
            }
        }

        override fun unbindView(item: TimerBinder) {
            nameLabel.text = ""
            hourlyCostLabel.text = ""
            totalCostLabel.text = ""

            timer.setOnStateChangedListener(null)
            timer.setTimerTickListener(null)
            timer.setChronometerState(PausableChronometer.State.EMPTY, 0L)
        }
    }

    override val layoutRes: Int
        get() = R.layout.timers_list_item
    override val type: Int
        get() = R.id.timer_item

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}

fun <T, Y> List<T>.toBinderArrayList(constructor: (T) -> Y): java.util.ArrayList<Y> {
    val bindersList: java.util.ArrayList<Y> = arrayListOf()
    this.forEach {
        bindersList.add(constructor(it))
    }
    return bindersList
}