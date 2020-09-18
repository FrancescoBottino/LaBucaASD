package it.uniparthenope.francescobottino001.labucaasd.persistence

import androidx.room.*

@Dao
interface TimerDAO {
    @Query("SELECT * FROM timer")
    fun getAll(): List<TimerData>

    @Query("SELECT * FROM timer WHERE id == :id")
    fun get(id: Long): TimerData

    @Insert
    fun insert(timerData: TimerData)

    @Update
    fun update(timerData: TimerData)

    @Delete
    fun delete(timerData: TimerData)
}