package it.uniparthenope.francescobottino001.labucaasd.persistence

class TimerRepository(private val dao: TimerDAO) {

    suspend fun getAll(): List<TimerData> {
        return dao.getAll()
    }

    suspend fun get(id: Long): TimerData {
        return dao.get(id)
    }

    suspend fun insert(timerData: TimerData) {
        dao.insert(timerData)
    }

    suspend fun update(timerData: TimerData) {
        dao.update(timerData)
    }

    suspend fun delete(timerData: TimerData) {
        dao.delete(timerData)
    }
}