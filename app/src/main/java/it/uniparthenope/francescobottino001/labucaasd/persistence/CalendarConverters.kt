package it.uniparthenope.francescobottino001.labucaasd.persistence

import androidx.room.TypeConverter
import java.util.*

object CalendarConverters {

    @TypeConverter
    @JvmStatic
    fun calendarToLong(c: Calendar?): Long? {
        return c?.let {
            c.timeZone = TimeZone.getTimeZone("UTC")
            c.timeInMillis
        }
    }

    @TypeConverter
    @JvmStatic
    fun longToCalendar(l: Long?): Calendar? {
        return l?.let {
            val c = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            c.timeInMillis = l
            c.timeZone = TimeZone.getDefault()
            c
        }
    }
}