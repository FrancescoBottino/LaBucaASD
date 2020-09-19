package it.uniparthenope.francescobottino001.labucaasd.activities

import android.view.View
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.swipe.ISwipeable
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import java.util.*

class TimerBinder(
    val timerData: TimerData
) : AbstractItem<TimerBinderViewHolder>(), ISwipeable, IDraggable {

    companion object {
        var updateCallback: ((TimerData) -> Unit)? = null
        var editCallback: ((TimerBinder) -> Unit)? = null
        var deleteCallback: ((TimerBinder) -> Unit)? = null

        var editTimerCallback: ((TimerBinder, PausableChronometer) -> Unit)? = null

        fun List<TimerData>.toBinderArrayList(): ArrayList<TimerBinder> {
            val bindersList: ArrayList<TimerBinder> = arrayListOf()
            this.forEach {
                bindersList.add(TimerBinder(it))
            }
            return bindersList
        }
    }

    override val layoutRes: Int
        get() = R.layout.timers_list_item
    override val type: Int
        get() = R.id.timer_item

    override fun getViewHolder(v: View): TimerBinderViewHolder = TimerBinderViewHolder(v)
    override val isSwipeable: Boolean = true
    override val isDraggable: Boolean = true
}