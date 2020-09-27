package it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDrawerDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.itemanimators.AlphaInAnimator
import com.skydoves.transformationlayout.OnTransformFinishListener
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import it.uniparthenope.francescobottino001.labucaasd.*
import it.uniparthenope.francescobottino001.labucaasd.activities.main.MainViewModel
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.timers_list_fragment.*

//TODO when edit, notify data changed only after animation AND room finished
//TODO on new timer, transform form into timer item in recyclerview
//TODO on timer stop trigger dialog for report saving (with preference on "don't ask anymore")
//TODO make form card clickable to consume click event and prevent overlay being clicked through the form

class TimersListFragment: BaseFragment(), ItemTouchCallback {

    companion object {
        fun newInstance() = TimersListFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private val adapter: FastItemAdapter<TimerBinder> = FastItemAdapter()
    private val timerBinderBuilder: TimerBinder.Builder = TimerBinder.Builder().apply {
        withEditCallback(::showEditTimerDialog)
        withDeleteCallback(::showDeleteTimerDialog)
        withSaveStateCallback(::updateTimerState)
        withEditChronometerCallback(::setTime)
    }
    private lateinit var form: FormView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.timers_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        form = FormView(timer_form, overlay)

        fab.setOnClickListener{
            showNewTimerDialog()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getListaTimer { timers ->
            adapter.set(
                timerBinderBuilder.build(timers)
            )
        }
    }

    override fun onBackPressed(): Boolean {
        return if(form.isShowing) {
            form.hide()
            true
        } else false
    }

    override fun itemTouchStartDrag(viewHolder: RecyclerView.ViewHolder) {
        super.itemTouchStartDrag(viewHolder)
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        DragDropUtil.onMove(adapter.itemAdapter, oldPosition, newPosition)
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        super.itemTouchDropped(oldPosition, newPosition)
        updateItemsPositions()
    }

    private fun updateItemsPositions() {
        adapter.adapterItems.forEach {
            val timer = it.timerData
            timer.ordinal = adapter.adapterItems.indexOf(it) + 1
            updateTimerState(timer)
        }
    }

    private fun newTimer(name: String, hourlyCost: Double) {
        val ordinal = adapter.adapterItems.size + 1
        val newTimer = TimerData(name, hourlyCost, ordinal)

        viewModel.addTimer(newTimer) { it ->
            adapter.itemAdapter.add(
                timerBinderBuilder.build(it)
            )
        }
    }

    private fun updateTimerState(timerData: TimerData, callBack:((TimerData) -> Unit)? = null) {
        viewModel.updateTimer(timerData, callBack)
    }

    private fun updateTimerStateAndNotifyAdapter(item: TimerBinder, callBack:((TimerData) -> Unit)? = null) {
        viewModel.updateTimer(item.timerData) {
            adapter.notifyItemChanged(adapter.adapterItems.indexOf(item))
            callBack?.invoke(it)
        }
    }

    private fun deleteTimerAndNotifyAdapter(item: TimerBinder, callBack:(() -> Unit)? = null) {
        viewModel.deleteTimer(item.timerData) {
            adapter.remove(adapter.adapterItems.indexOf(item))
            updateItemsPositions()
            callBack?.invoke()
        }
    }

    private fun showNewTimerDialog() {
        form.currentTransformation = fab_transformation_layout

        timer_form.setUpLayout(
            TimerForm.FORM_TYPE.NEW_TIMER, {
                try {
                    timer_form.getFormData(::newTimer)
                    form.hide()
                } catch (ignored: Exception) {}
            }, {
                form.hide()
            }
        )

        form.show()
    }

    private fun showEditTimerDialog(item: TimerBinder, vh: TimerBinderViewHolder) {
        form.currentTransformation = vh.transformationLayout
        form.currentTransformation?.onTransformFinishListener = object : OnTransformFinishListener {
            override fun onFinish(isTransformed: Boolean) {
                adapter.notifyItemChanged(adapter.adapterItems.indexOf(item))
            }
        }

        fun FormView.showComposite() {
            this.show()
            vh.drawer.fadeOut(
                vh.transformationLayout.duration
            )
        }

        fun FormView.hideComposite() {
            this.hide()
            vh.drawer.fadeIn(
                vh.transformationLayout.duration
            )
        }

        timer_form.setUpLayout(
            TimerForm.FORM_TYPE.EDIT_TIMER, {
                try {
                    timer_form.getFormData { name, hourlyCost ->
                        item.timerData.name = name
                        item.timerData.hourlyCost = hourlyCost
                        updateTimerStateAndNotifyAdapter(item) {
                            form.hideComposite()
                        }
                    }
                } catch (ignored: Exception) {}
            }, {
                form.hideComposite()
            }, item.timerData
        )

        form.showComposite()
    }

    private fun showDeleteTimerDialog(item: TimerBinder, vh: TimerBinderViewHolder) {
        form.currentTransformation = vh.transformationLayout

        fun FormView.showComposite() {
            this.show()
            vh.drawer.fadeOut(
                vh.transformationLayout.duration
            )
        }

        fun FormView.hideComposite() {
            this.hide()
            vh.drawer.fadeIn(
                vh.transformationLayout.duration
            )
        }

        timer_form.setUpLayout(
            TimerForm.FORM_TYPE.DELETE_TIMER, {
                vh.setIsRecyclable(false)

                val speed = 120L
                overlay.fadeOut(speed)
                timer_form.scaleOut(speed)
                    .withEndAction {
                        timer_form.visibility = View.GONE
                        timer_form.scaleX = 1f
                        timer_form.scaleY = 1f
                        timer_form.cleanForm()
                    }

                deleteTimerAndNotifyAdapter(item)
            }, {
                form.hideComposite()
            }
        )

        form.showComposite()
    }

    //TODO animation
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
                updateTimerState(item.timerData)
            }, h, m, s, true).show()
        }
    }
}