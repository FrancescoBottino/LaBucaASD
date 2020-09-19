package it.uniparthenope.francescobottino001.labucaasd.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDrawerDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.itemanimators.AlphaInAnimator
import it.uniparthenope.francescobottino001.labucaasd.BasicActivity
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.activities.TimerBinder.Companion.toBinderArrayList
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.activity_main.*

//TODO TIMER LONGCLICK SET TIME

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

        val touchCallback = SimpleSwipeDrawerDragCallback(this)
            .withNotifyAllDrops(true)
            .withSwipeRight(160) // each button (delete, edit) takes 80dp, for a total of 160
            .withSwipeLeft(0)
            .withSensitivity(10f)

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(timers_list)

        TimerBinder.updateCallback = viewModel::updateTimer
        TimerBinder.deleteCallback = this::deleteTimer
        TimerBinder.editCallback = this::editTimer
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

    fun newTimer() {
        NewTimerDialog(this) { name, hourlyCost ->
            val ordinal = adapter.adapterItems.size + 1
            val newTimer = TimerData(name, hourlyCost, ordinal)

            adapter.itemAdapter.add(TimerBinder(newTimer))
            viewModel.addTimer(newTimer)
        }.show()
    }

    fun editTimer(item: TimerBinder) {
        EditTimerDialog(this, item.timerData) { name, hourlyCost ->
            item.timerData.name = name
            item.timerData.hourlyCost = hourlyCost
            adapter.notifyItemChanged( adapter.adapterItems.indexOf(item) )
            viewModel.updateTimer(item.timerData)
        }.show()
    }

    fun deleteTimer(item: TimerBinder) {
        DeleteTimerDialog(this) {
            adapter.remove( adapter.adapterItems.indexOf(item) )
            viewModel.deleteTimer(item.timerData)
        }.show()
    }
}