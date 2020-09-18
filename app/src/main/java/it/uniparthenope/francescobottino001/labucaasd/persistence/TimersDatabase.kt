package it.uniparthenope.francescobottino001.labucaasd.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TimerData::class], version = 1)
@TypeConverters(CalendarConverters::class, StateConverters::class)
abstract class TimersDatabase : RoomDatabase() {
    abstract fun timerDao(): TimersDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TimersDatabase? = null

        fun getDatabase(context: Context): TimersDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TimersDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }

        private const val DATABASE_NAME: String = "timers_database"
    }
}