package it.uniparthenope.francescobottino001.labucaasd.persistence

import androidx.room.TypeConverter
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer

object StateConverters {

    @TypeConverter
    @JvmStatic
    fun stateToString(state: PausableChronometer.State?): String? {
        return state?.name
    }

    @TypeConverter
    @JvmStatic
    fun stringToState(state_string: String?): PausableChronometer.State? {
        return state_string?.let { PausableChronometer.State.valueOf(it) }
    }
}