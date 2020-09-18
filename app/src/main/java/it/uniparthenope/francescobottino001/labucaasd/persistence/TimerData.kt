package it.uniparthenope.francescobottino001.labucaasd.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import it.uniparthenope.francescobottino001.chronometers_extensions.PausableChronometer
import java.util.*

/**
 * State > calendar > seconds
 */

@Entity(tableName = "timer")
data class TimerData(
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "hourly_cost")
    var hourlyCost: Double,
    @ColumnInfo(name = "ordinal")
    var ordinal: Long,
    @ColumnInfo(name = "saved_at")
    var savedAt: Calendar? = null,
    @ColumnInfo(name = "elapsed_seconds")
    var elapsedSeconds: Long? = null,
    @ColumnInfo(name = "state")
    var state: PausableChronometer.State? = null,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
)