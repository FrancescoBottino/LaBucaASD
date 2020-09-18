package it.uniparthenope.francescobottino001.labucaasd.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.itemanimators.AlphaInAnimator
import it.uniparthenope.francescobottino001.labucaasd.BasicActivity
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.activities.TimerBinder.Companion.toBinderArrayList
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class MainActivity : BasicActivity(), ItemTouchCallback {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var adapter: FastItemAdapter<GenericItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FastItemAdapter()

        timers_list.layoutManager = LinearLayoutManager(this)
        timers_list.itemAnimator = AlphaInAnimator()
        timers_list.adapter = adapter

        val dragCallback = SimpleDragCallback(this)
        val touchHelper = ItemTouchHelper(dragCallback)
        touchHelper.attachToRecyclerView(timers_list)

        TimerBinder.saveCallback = viewModel::updateTimer
        viewModel.getListaTimer { timers ->
            adapter.set(timers.toBinderArrayList())
        }

        //TODO SWIPE LEFT DELETE
        // WITH DIALOG

        //TODO SWIPE RIGHT EDIT
        // WITH DIALOG

        fab.setOnClickListener {
            NewTimerDialog(this) { name, hourlyCost ->
                val ordinal = adapter.adapterItems.size + 1
                val newTimer = TimerData(name, hourlyCost, ordinal)

                adapter.itemAdapter.add(TimerBinder(newTimer))
                viewModel.addTimer(newTimer)
            }.show()
        }
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        DragDropUtil.onMove(adapter.itemAdapter, oldPosition, newPosition)

        adapter.adapterItems.forEach {
            val timer = (it as TimerBinder).timerData
            timer.ordinal = adapter.adapterItems.indexOf(it) + 1
            viewModel.updateTimer(timer)
        }

        return true
    }
}