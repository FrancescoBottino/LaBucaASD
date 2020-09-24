package it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list

import android.view.View
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.swipe.ISwipeable
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData

class TimerBinder private constructor(
    val timerData: TimerData
) : AbstractItem<TimerBinderViewHolder>(), ISwipeable, IDraggable {

    class Builder {
        fun build(timerData: TimerData): TimerBinder {
            return TimerBinder(timerData)
                .withEditCallback(editCallback)
                .withSaveStateCallback(saveStateCallback)
                .withDeleteCallback(deleteCallback)
                .withEditChronometerCallback(editChronometerCallback)
        }

        fun build(timerDataList: List<TimerData>): List<TimerBinder> {
            return timerDataList.map { build(it) }
        }

        var saveStateCallback: ((TimerData) -> Unit)? = null
        fun withSaveStateCallback(callback: ((TimerData) -> Unit)?): Builder {
            this.saveStateCallback = callback
            return this
        }

        var editCallback: ((TimerBinder, TimerBinderViewHolder) -> Unit)? = null
        fun withEditCallback(callback: ((TimerBinder, TimerBinderViewHolder) -> Unit)?): Builder {
            this.editCallback = callback
            return this
        }

        var deleteCallback: ((TimerBinder, TimerBinderViewHolder) -> Unit)? = null
        fun withDeleteCallback(callback: ((TimerBinder, TimerBinderViewHolder) -> Unit)?): Builder {
            this.deleteCallback = callback
            return this
        }

        var editChronometerCallback: ((TimerBinder, PausableChronometer) -> Unit)? = null
        fun withEditChronometerCallback(callback: ((TimerBinder, PausableChronometer) -> Unit)?): Builder {
            this.editChronometerCallback = callback
            return this
        }
    }

    var saveStateCallback: ((TimerData) -> Unit)? = null
    fun withSaveStateCallback(callback: ((TimerData) -> Unit)?): TimerBinder {
        this.saveStateCallback = callback
        return this
    }

    var editCallback: ((TimerBinder, TimerBinderViewHolder) -> Unit)? = null
    fun withEditCallback(callback: ((TimerBinder, TimerBinderViewHolder) -> Unit)?): TimerBinder {
        this.editCallback = callback
        return this
    }

    var deleteCallback: ((TimerBinder, TimerBinderViewHolder) -> Unit)? = null
    fun withDeleteCallback(callback: ((TimerBinder, TimerBinderViewHolder) -> Unit)?): TimerBinder {
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