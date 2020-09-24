package it.uniparthenope.francescobottino001.labucaasd.activities.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimersDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val app: Application): AndroidViewModel(app) {
    private val db: TimersDatabase by lazy {
        TimersDatabase.getDatabase(app)
    }

    fun getListaTimer(onComplete: (List<TimerData>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val allTimers = db.timerDao().getAll()

            GlobalScope.launch(Dispatchers.Main) {
                onComplete.invoke(allTimers)
            }
        }
    }

    fun addTimer(timer: TimerData, onComplete: ((TimerData)->Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            timer.id = db.timerDao().insert(timer).toInt()

            GlobalScope.launch(Dispatchers.Main) {
                onComplete?.invoke(timer)
            }
        }
    }

    fun updateTimer(timer: TimerData, onComplete: ((TimerData)->Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            db.timerDao().update(timer)

            GlobalScope.launch(Dispatchers.Main) {
                onComplete?.invoke(timer)
            }
        }
    }

    fun deleteTimer(timer: TimerData, onComplete: (()->Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            db.timerDao().delete(timer)

            GlobalScope.launch(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }
}