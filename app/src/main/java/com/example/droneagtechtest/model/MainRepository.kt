package com.example.droneagtechtest.model

import androidx.lifecycle.LiveData
import com.example.droneagtechtest.model.db.PolygonDao
import com.example.droneagtechtest.model.db.PolygonDatabase
import com.example.droneagtechtest.model.db.PolygonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository {
    private val polygonDao: PolygonDao

    init {
        val polygonDatabase = PolygonDatabase.getDatabase()
        polygonDao = polygonDatabase.polygonDao()
    }

    suspend fun insert(polygon: PolygonEntity) {
        polygonDao.insert(polygon)
    }

    fun getLast(): PolygonEntity? {
        return polygonDao.getLast()
    }

    fun getAll(): LiveData<List<PolygonEntity>> {
        return polygonDao.getAll()
    }

    fun getCount(): Int {
        return polygonDao.getCount()
    }

    fun clearAll() {
        polygonDao.clearAll()
    }
}