package com.example.droneagtechtest.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "polygons")
data class PolygonEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "points")
    var points: String,
    @ColumnInfo(name = "center_point")
    var centerPoint: String,
    @ColumnInfo(name = "area")
    var area: String
)
