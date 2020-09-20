package it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list

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
        fun List<TimerData>.toBinderArrayList(): ArrayList<TimerBinder> {
            val bindersList: ArrayList<TimerBinder> = arrayListOf()
            this.forEach {
                bindersList.add(TimerBinder(it))
            }
            return bindersList
        }

        fun ArrayList<TimerBinder>.withUpdateCallback(
            callback: ((TimerData) -> Unit)?
        ): ArrayList<TimerBinder> {
            this.forEach {
                it.updateCallback = callback
            }
            return this
        }

        fun ArrayList<TimerBinder>.withEditCallback(
            callback: ((TimerBinder) -> Unit)?
        ): ArrayList<TimerBinder> {
            this.forEach {
                it.editCallback = callback
            }
            return this
        }

        fun ArrayList<TimerBinder>.withDeleteCallback(
            callback: ((TimerBinder) -> Unit)?
        ): ArrayList<TimerBinder> {
            this.forEach {
                it.deleteCallback = callback
            }
            return this
        }

        fun ArrayList<TimerBinder>.withEditChronometerCallback(
            callback: ((TimerBinder, PausableChronometer) -> Unit)?
        ): ArrayList<TimerBinder> {
            this.forEach {
                it.editChronometerCallback = callback
            }
            return this
        }
    }

    var updateCallback: ((TimerData) -> Unit)? = null
    fun withUpdateCallback(callback: ((TimerData) -> Unit)?): TimerBinder {
        this.updateCallback = callback
        return this
    }

    var editCallback: ((TimerBinder) -> Unit)? = null
    fun withEditCallback(callback: ((TimerBinder) -> Unit)?): TimerBinder {
        this.editCallback = callback
        return this
    }

    var deleteCallback: ((TimerBinder) -> Unit)? = null
    fun withDeleteCallback(callback: ((TimerBinder) -> Unit)?): TimerBinder {
        this.deleteCallback = callback
        return this
    }

    var editChronometerCallback: ((TimerBinder, PausableChronometer) -> Unit)? = null
    fun withEditChronometerCallback(callback: ((TimerBinder, PausableChronometer) -> Unit)?): TimerBinder {
        this.editChronometerCallback = callback
        return this
    }

    override val layoutRes: Int
        get() = R.layout.timers_list_item
    override val type: Int
        get() = R.id.timer_item

    override fun getViewHolder(v: View): TimerBinderViewHolder = TimerBinderViewHolder(v)
    override val isSwipeable: Boolean = true
    override val isDraggable: Boolean = true
}