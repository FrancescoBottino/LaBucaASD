package it.uniparthenope.francescobottino001.labucaasd.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDrawerDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.itemanimators.AlphaInAnimator
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import it.uniparthenope.francescobottino001.labucaasd.BasicActivity
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.activities.TimerBinder.Companion.toBinderArrayList
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.activity_main.*

//TODO IMPROVE DIALOG FORM INTERFACE

class MainActivity : BasicActivity(), ItemTouchCallback {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var adapter: FastItemAdapter<TimerBinder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FastItemAdapter()

        timers_list.layoutManager = LinearLayoutManager(this)
        timers_list.itemAnimator = AlphaInAnimator()
        timers_list.adapter = adapter

        val touchCallback = SimpleSwipeDrawerDragCallback(
            this,
            ItemTouchHelper.RIGHT)
            .withNotifyAllDrops(true)
            .withSwipeRight(160) // each button (delete, edit) takes 80dp, for a total of 160
            .withSwipeLeft(0)
            .withSensitivity(10f)

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(timers_list)

        TimerBinder.updateCallback = viewModel::updateTimer
        TimerBinder.deleteCallback = this::deleteTimer
        TimerBinder.editCallback = this::editTimer
        TimerBinder.editTimerCallback = this::setTime

        viewModel.getListaTimer { timers ->
            adapter.set(timers.toBinderArrayList())
        }

        fab.setOnClickListener{ newTimer() }
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        DragDropUtil.onMove(adapter.itemAdapter, oldPosition, newPosition)
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        super.itemTouchDropped(oldPosition, newPosition)

        adapter.adapterItems.forEach {
            val timer = it.timerData
            timer.ordinal = adapter.adapterItems.indexOf(it) + 1
            viewModel.updateTimer(timer)
        }
    }

    private fun newTimer() {
        NewTimerDialog(this) { name, hourlyCost ->
            val ordinal = adapter.adapterItems.size + 1
            val newTimer = TimerData(name, hourlyCost, ordinal)

            viewModel.addTimer(newTimer) { it ->
                adapter.itemAdapter.add(TimerBinder(it))
            }
        }.show()
    }

    private fun editTimer(item: TimerBinder) {
        EditTimerDialog(this, item.timerData) { name, hourlyCost ->
            item.timerData.name = name
            item.timerData.hourlyCost = hourlyCost
            adapter.notifyItemChanged( adapter.adapterItems.indexOf(item) )
            viewModel.updateTimer(item.timerData)
        }.show()
    }

    private fun deleteTimer(item: TimerBinder) {
        DeleteTimerDialog(this) {
            adapter.remove( adapter.adapterItems.indexOf(item) )
            viewModel.deleteTimer(item.timerData)
        }.show()
    }

    private fun setTime(item: TimerBinder, chronometer: PausableChronometer) {
        val h = (chronometer.totalElapsedSeconds / 3600).toInt()
        val m = (chronometer.totalElapsedSeconds - (h * 3600)).toInt() / 60
        val s = (chronometer.totalElapsedSeconds - (h * 3600) - (m * 60)).toInt()

        MyTimePickerDialog(this, { _, hh, mm, ss ->
            val totalSeconds = ss.toLong() + mm.toLong()*60 + hh.toLong()*3600
            var state = chronometer.currentState
            if( state == PausableChronometer.State.EMPTY && totalSeconds != 0L ) {
                state = PausableChronometer.State.IDLE
            }
            chronometer.setChronometerState( state, totalSeconds )
            TimerBinder.updateCallback?.invoke(item.timerData)
        }, h, m, s, true).show()
    }
}