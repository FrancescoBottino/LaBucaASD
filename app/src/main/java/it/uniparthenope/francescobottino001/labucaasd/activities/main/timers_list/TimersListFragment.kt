package it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDrawerDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.itemanimators.AlphaInAnimator
import com.skydoves.transformationlayout.onTransformationStartContainer
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.activities.main.MainViewModel
import it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list.TimerBinder.Companion.toBinderArrayList
import it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list.TimerBinder.Companion.withDeleteCallback
import it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list.TimerBinder.Companion.withEditCallback
import it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list.TimerBinder.Companion.withEditChronometerCallback
import it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list.TimerBinder.Companion.withUpdateCallback
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.timers_list_fragment.*

class TimersListFragment: Fragment(), ItemTouchCallback {

    companion object {
        fun newInstance() = TimersListFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: FastItemAdapter<TimerBinder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onTransformationStartContainer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.timers_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = FastItemAdapter()

        val touchCallback = SimpleSwipeDrawerDragCallback(
            this,
            ItemTouchHelper.RIGHT)
            .withNotifyAllDrops(true)
            .withSwipeRight(160) // each button (delete, edit) takes 80dp, for a total of 160
            .withSwipeLeft(0)
            .withSensitivity(5f)

        val touchHelper = ItemTouchHelper(touchCallback)

        timers_list.layoutManager = LinearLayoutManager(root.context)
        timers_list.itemAnimator = AlphaInAnimator()
        timers_list.adapter = adapter

        touchHelper.attachToRecyclerView(timers_list)

        fab.setOnClickListener{
            timer_form.showFormCallback = {
                screen.animate().apply {
                    alpha(1f)
                    duration = 550L
                    setListener(
                        object: Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator?) {
                                screen.visibility = View.VISIBLE
                            }
                            override fun onAnimationEnd(p0: Animator?) {}
                            override fun onAnimationCancel(p0: Animator?) {}
                            override fun onAnimationRepeat(p0: Animator?) {}
                        }
                    )
                }
                fab_to_new_timer_transformation_layout.startTransform()
            }
            timer_form.dismissFormCallback = {
                fab_to_new_timer_transformation_layout.finishTransform()
                screen.animate().apply {
                    alpha(0.0f)
                    duration = 550L
                    setListener(
                        object: Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator?) {}
                            override fun onAnimationEnd(p0: Animator?) {
                                screen.visibility = View.GONE
                            }
                            override fun onAnimationCancel(p0: Animator?) {}
                            override fun onAnimationRepeat(p0: Animator?) {}
                        }
                    )
                }
            }
            timer_form.setUpLayout(
                TimerForm.FORM_TYPE.NEW_TIMER, {
                    try {
                        timer_form.getFormData(::newTimer)
                        timer_form.dismiss()
                    } catch (ignored: Exception) {}
                }, {
                    timer_form.dismiss()
                }
            )
            timer_form.show()
        }

        screen.setOnClickListener {
            timer_form.dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getListaTimer { timers ->
            adapter.set(
                timers.toBinderArrayList()
                    .withEditCallback(this::editTimer)
                    .withDeleteCallback(this::deleteTimer)
                    .withUpdateCallback(this::updateTimer)
                    .withEditChronometerCallback(this::setTime)
            )
        }
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
            updateTimer(timer)
        }
    }

    private fun newTimer(name: String, hourlyCost: Double) {
        val ordinal = adapter.adapterItems.size + 1
        val newTimer = TimerData(name, hourlyCost, ordinal)

        viewModel.addTimer(newTimer) { it ->
            adapter.itemAdapter.add(TimerBinder(it))
        }
    }

    private fun updateTimer(timerData: TimerData) {
        viewModel.updateTimer(timerData)
    }

    private fun editTimer(item: TimerBinder) {
        activity?.let { ctx ->
            EditTimerDialog(ctx, item.timerData) { name, hourlyCost ->
                item.timerData.name = name
                item.timerData.hourlyCost = hourlyCost
                viewModel.updateTimer(item.timerData) { it ->
                    adapter.notifyItemChanged(adapter.adapterItems.indexOf(item))
                }
            }.show()
        }
    }

    private fun deleteTimer(item: TimerBinder) {
        activity?.let { ctx ->
            DeleteTimerDialog(ctx) {
                viewModel.deleteTimer(item.timerData) {
                    adapter.remove(adapter.adapterItems.indexOf(item))
                }
            }.show()
        }
    }

    private fun setTime(item: TimerBinder, chronometer: PausableChronometer) {
        activity?.let { ctx ->
            val h = (chronometer.totalElapsedSeconds / 3600).toInt()
            val m = (chronometer.totalElapsedSeconds - (h * 3600)).toInt() / 60
            val s = (chronometer.totalElapsedSeconds - (h * 3600) - (m * 60)).toInt()

            MyTimePickerDialog(ctx, { _, hh, mm, ss ->
                val totalSeconds = ss.toLong() + mm.toLong() * 60 + hh.toLong() * 3600
                var state = chronometer.currentState
                if (state == PausableChronometer.State.EMPTY && totalSeconds != 0L) {
                    state = PausableChronometer.State.IDLE
                }
                chronometer.setChronometerState(state, totalSeconds)
                updateTimer(item.timerData)
            }, h, m, s, true).show()
        }
    }
}