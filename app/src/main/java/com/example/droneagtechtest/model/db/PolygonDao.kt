package com.example.droneagtechtest.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PolygonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(polygon: PolygonEntity)

    @Query("DELETE FROM polygons")
    fun clearAll()

    @Query("SELECT * FROM polygons ORDER BY id DESC")
    fun getAll(): LiveData<List<PolygonEntity>>

    @Query("SELECT * FROM polygons ORDER BY id DESC LIMIT 1")
    fun getLast(): PolygonEntity?

    @Query("SELECT COUNT(*) FROM polygons")
    fun getCount(): Int
}