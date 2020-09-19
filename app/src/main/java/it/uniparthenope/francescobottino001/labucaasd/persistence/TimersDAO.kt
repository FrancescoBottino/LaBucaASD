package it.uniparthenope.francescobottino001.labucaasd.persistence

import androidx.room.*

@Dao
interface TimersDAO {
    @Query("SELECT * FROM timer ORDER BY ordinal ASC")
    suspend fun getAll(): List<TimerData>

    @Query("SELECT * FROM timer WHERE id == :id")
    suspend fun get(id: Int): TimerData

    @Insert
    suspend fun insert(timerData: TimerData): Long

    @Update
    suspend fun update(timerData: TimerData)

    @Delete
    suspend fun delete(timerData: TimerData)
}