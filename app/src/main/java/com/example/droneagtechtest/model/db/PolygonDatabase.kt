package com.example.droneagtechtest.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PolygonEntity::class], version = 1, exportSchema = false)
abstract class PolygonDatabase: RoomDatabase() {
    abstract fun polygonDao(): PolygonDao

    companion object {
        @Volatile
        private var INSTANCE: PolygonDatabase? = null

        fun init(context: Context) {
            INSTANCE ?: synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    PolygonDatabase::class.java,
                    "polygon_database")
                    .build()
            }
        }

        fun getDatabase(): PolygonDatabase {
            return INSTANCE ?: throw IllegalStateException("Database not initialized")
        }
    }
}