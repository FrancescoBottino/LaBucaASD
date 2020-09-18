package it.uniparthenope.francescobottino001.labucaasd.activities

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

    fun getListaTimer(callBack: (List<TimerData>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val allTimers = db.timerDao().getAll()

            GlobalScope.launch(Dispatchers.Main) {
                callBack.invoke(allTimers)
            }
        }
    }

    fun addTimer(timer: TimerData) {
        viewModelScope.launch(Dispatchers.IO) {
            db.timerDao().insert(timer)
        }
    }

    fun updateTimer(timer: TimerData) {
        viewModelScope.launch(Dispatchers.IO) {
            db.timerDao().update(timer)
        }
    }

    fun deleteTimer(timer: TimerData) {
        viewModelScope.launch(Dispatchers.IO) {
            db.timerDao().delete(timer)
        }
    }
}