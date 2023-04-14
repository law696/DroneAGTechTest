package com.example.droneagtechtest

import android.app.Application
import androidx.room.Room
import com.example.droneagtechtest.model.db.PolygonDatabase

class MapApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PolygonDatabase.init(this)
    }
}