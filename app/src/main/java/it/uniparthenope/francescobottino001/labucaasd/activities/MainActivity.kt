package it.uniparthenope.francescobottino001.labucaasd.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.itemanimators.AlphaInAnimator
import it.uniparthenope.francescobottino001.labucaasd.BasicActivity
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.activities.TimerBinder.Companion.toBinderArrayList
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.activity_main.*
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
            adapter.set(timersList.toBinderArrayList())
        }

        //TODO SWIPE LEFT DELETE
        // WITH DIALOG

        //TODO SWIPE RIGHT EDIT
        // WITH DIALOG

        //TODO DRAG TO REORDER

        fab.setOnClickListener {
            //TODO DIALOG NEW TIMER
            //newTimerDialog()
        }
    }

    /*
    fun newTimer() {
        val ordinal = timersList.size + 1
        val newTimer = TimerData("PlayStation", 20.0, ordinal.toLong())
        timersList.add(newTimer)

        adapter.itemAdapter.add(TimerBinder(newTimer))

        viewModel.addTimer(newTimer)
    }
     */
}