package it.uniparthenope.francescobottino001.labucaasd.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import com.mikepenz.itemanimators.AlphaInAnimator
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import it.uniparthenope.francescobottino001.labucaasd.BasicActivity
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.activities.TimerBinder.Companion.toBinderArrayList
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.timers_list_item.*

class MainActivity : BasicActivity(), ItemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var adapter: FastItemAdapter<GenericItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FastItemAdapter()

        timers_list.layoutManager = LinearLayoutManager(this)
        timers_list.itemAnimator = AlphaInAnimator()
        timers_list.adapter = adapter


        val leaveBehindDrawableLeft =
            ContextCompat.getDrawable(this, R.drawable.ic_round_delete_24)

        val laveBehindDrawableRight =
            ContextCompat.getDrawable(this, R.drawable.ic_round_edit_24)

        val dragCallback = SimpleSwipeDragCallback(
            this,
            this,
            null
        )
            .withBackgroundSwipeLeft(Color.TRANSPARENT)
            .withLeaveBehindSwipeLeft(leaveBehindDrawableLeft!!)
            .withBackgroundSwipeRight(Color.TRANSPARENT)
            .withLeaveBehindSwipeRight(laveBehindDrawableRight!!)
            .withNotifyAllDrops(true)
            .withSensitivity(10f)
            .withSurfaceThreshold(0.3f)

        val touchHelper = ItemTouchHelper(dragCallback)
        touchHelper.attachToRecyclerView(timers_list)

        TimerBinder.saveCallback = viewModel::updateTimer
        viewModel.getListaTimer { timers ->
            adapter.set(timers.toBinderArrayList())
        }

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
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        super.itemTouchDropped(oldPosition, newPosition)

        adapter.adapterItems.forEach {
            val timer = (it as TimerBinder).timerData
            timer.ordinal = adapter.adapterItems.indexOf(it) + 1
            viewModel.updateTimer(timer)
        }
    }

    override fun itemSwiped(position: Int, direction: Int) {
        adapter.notifyItemChanged(position)

        val timerData = (adapter.adapterItems[position] as TimerBinder).timerData

        if(direction == ItemTouchHelper.LEFT) {
            DeleteTimerDialog(this) {
                adapter.remove(position)
                viewModel.deleteTimer(timerData)
            }.show()
        } else if(direction == ItemTouchHelper.RIGHT) {
            EditTimerDialog(this, timerData) { name, hourlyCost ->
                timerData.name = name
                timerData.hourlyCost = hourlyCost
                adapter.notifyItemChanged(position)
                viewModel.updateTimer(timerData)
            }.show()
        }
    }

    /*
    override fun itemSwiped(position: Int, direction: Int) {
        // -- Option 1: Direct action --
        //do something when swiped such as: select, remove, update, ...:
        //A) fastItemAdapter.select(position);
        //B) fastItemAdapter.remove(position);
        //C) update item, set "read" if an email etc

        // -- Option 2: Delayed action --
        val item = fastItemAdapter.getItem(position) ?: return
        item.swipedDirection = direction

        // This can vary depending on direction but remove & archive simulated here both results in
        // removal from list
        val message = Random().nextInt()
        deleteHandler.sendMessageDelayed(Message.obtain().apply { what = message; obj = item }, 3000)

        item.swipedAction = Runnable {
            deleteHandler.removeMessages(message)

            item.swipedDirection = 0
            val position1 = fastItemAdapter.getAdapterPosition(item)
            if (position1 != RecyclerView.NO_POSITION) {
                fastItemAdapter.notifyItemChanged(position1)
            }
        }

        fastItemAdapter.notifyItemChanged(position)
        //TODO can this above be made more generic, along with the support in the item?
    }
     */
}