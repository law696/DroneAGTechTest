package com.example.droneagtechtest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.droneagtechtest.model.MainRepository
import com.example.droneagtechtest.model.db.PolygonEntity
import com.example.droneagtechtest.utils.PolygonUtils
import com.example.droneagtechtest.utils.StringUtils.latLngToString
import com.example.droneagtechtest.utils.StringUtils.listToString
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository): ViewModel() {

    /**
    Inserts a new polygon into the database. It takes a list of LatLng points as input and calculates
    the center point and area of the polygon using helper functions in PolygonUtils. It then creates
    a new PolygonEntity object with the string representation of the points, center point, and area,
    and inserts it into the database using the PolygonRepository.
     */
    fun insertPolygon(points: List<LatLng>) {
        viewModelScope.launch {
            val newPolygonEntity = PolygonEntity(
                points =  listToString(points),
                centerPoint = latLngToString(
                    PolygonUtils.calculatePolygonCenter(points)),
                area = PolygonUtils.calculatePolygonArea(points))
            repository.insert(newPolygonEntity)
        }
    }

    /*
     This function retrieves all the polygons saved in the database
     by calling the getAll() function from the repository.
     It returns a LiveData object containing a list of PolygonEntity
     objects, which can be observed for changes.
     */
    fun getAll(): LiveData<List<PolygonEntity>> {
       return repository.getAll()
    }

    /*
    This function returns the last polygon entity that was added
    to the database. If there are no polygons in the database,
     it returns null.
     */
    fun getLast(): PolygonEntity? {
        return repository.getLast()
    }
}