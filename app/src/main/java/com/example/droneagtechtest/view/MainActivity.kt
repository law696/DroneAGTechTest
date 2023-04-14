package com.example.droneagtechtest.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.droneagtechtest.R
import com.example.droneagtechtest.model.MainRepository
import com.example.droneagtechtest.model.db.PolygonEntity
import com.example.droneagtechtest.utils.StringUtils.stringToLatLng
import com.example.droneagtechtest.utils.StringUtils.stringToList
import com.example.droneagtechtest.viewmodel.MainViewModel
import com.example.droneagtechtest.viewmodel.MainViewModelFactory
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var addButton: Button
    private lateinit var retrieveButton: Button
    private lateinit var  viewModel: MainViewModel
    private lateinit var factory: MainViewModelFactory

    private var location: Location? = null
    private var polygon: Polygon? = null
    private var polygonPoints: MutableList<LatLng> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        addButton = findViewById(R.id.add_button)
        retrieveButton = findViewById(R.id.retrieve_button)

        setListeners()

        factory = MainViewModelFactory(MainRepository())

        viewModel =
            ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setListeners() {
        addButton.setOnClickListener(this)
        retrieveButton.setOnClickListener(this)
    }

    /**
    This function finishes the polygon drawing process
    by removing the current polygon from the map, inserting the
    polygon points into the database via a ViewModel, displaying a
    success message, and clearing the polygon points list.
     */
    private fun finishPolygon() {
        polygon?.remove()
        polygon = null

        if (polygonPoints.size > 0) {
            viewModel.insertPolygon(polygonPoints)
            Toast.makeText(this,
                "Polygon added to Database",
                Toast.LENGTH_SHORT).show()
            polygonPoints.clear()
        }
    }


    /**
    This function updates the Google Map with the last polygon that
    was added to the database. It converts the polygon points from
    a string to a list, creates a polygon with the points and adds it to the map.
    Then, it adjusts the camera to show the entire polygon on the screen.
    If there is no polygon to display (i.e., the argument polygon is null),
    nothing is done.
     */
    private fun updateMapWithLastPolygon(polygon: PolygonEntity?) {
        polygon?.let { polygonEntity ->
            val points = stringToList(polygonEntity.points)
            val polygonOptions = PolygonOptions()
                .addAll(points)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(128, 255, 0, 0))
            googleMap.apply {
                clear()
                addPolygon(polygonOptions)
            }

            val bounds = LatLngBounds.Builder().apply {
                for (point in points) {
                    include(point)
                }
            }.build()
            val cameraUpdate =
                CameraUpdateFactory.newLatLngBounds(bounds, 50)
            googleMap.animateCamera(cameraUpdate)
        }
    }

    /**
    Updates the Google Map with polygons stored in a LiveData object.
    The function clears the map and adds each polygon to it.
    Each polygon is created using the PolygonOptions class and is customized with
    a red stroke color and a semi-transparent red fill color.
    The function observes the LiveData object and is called every time the data changes.
    @param polygons a LiveData object that contains a list of PolygonEntity objects
     */
    private fun updateMap(polygons: LiveData<List<PolygonEntity>>) {
        polygons.observe(this) { polygonEntities ->
            googleMap.clear()
            for (polygonEntity in polygonEntities) {
                val points = stringToList(polygonEntity.points)
                val polygonOptions = PolygonOptions()
                    .addAll(points)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(128, 255, 0, 0))
                googleMap.addPolygon(polygonOptions)
            }
        }
    }

    /**
    Gets the user's current location and updates the map to display it.
    If the user has not granted permission to access their location,
    a permission request is initiated. If the location is already known,
    the map is updated immediately with the last saved polygon and a marker is added
    at the center of the polygon.
     */
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION)
            googleMap.isMyLocationEnabled = true
            return
        }

        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (locationResult.lastLocation == null) {
                      return
                    }
                    location = locationResult.lastLocation
                    val currentLatLng = LatLng(location!!.latitude, location!!.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))

                    val markerOptions = MarkerOptions()
                        .position(currentLatLng)
                        .title("My location")
                    googleMap.addMarker(markerOptions)
                }
            },
            null)

        if (location == null) {
            var polygon: PolygonEntity? = null
            CoroutineScope(Dispatchers.IO).launch {
                polygon = viewModel.getLast()
                withContext(Dispatchers.Main) {
                    updateMapWithLastPolygon(polygon)
                    addCenterMarker(polygon)
                }
            }
        }
    }

    /*
    This function adds a marker to the center point of the provided
    polygon entity. It first checks if the polygon is not null and if so,
    converts the center point string to a LatLng object. Then, it creates
    a MarkerOptions object with the position set to the centerLatLng
    and a title showing the area of the polygon.
    Finally, it adds the marker to the Google Map.
    If the polygon is null, it logs an error.
    */
    private fun addCenterMarker(polygon: PolygonEntity?) {
        polygon?.let {
            val centerLatLng = stringToLatLng(polygon.centerPoint)
            val markerOptions = MarkerOptions()
                .position(centerLatLng)
                .title("Area: ${polygon.area}")
            googleMap.addMarker(markerOptions)
        } ?: Log.e(MainActivity::class.java.name, "polygonEntity is null")
    }

    /*
    This function adds center markers to the Google Map for all the
     polygons in the LiveData<List<PolygonEntity>> argument.
     It converts the center point from a string to a LatLng, creates a
     marker at the center point with the area of the polygon as the title,
    and adds the marker to the map.
    */
    private fun addCenterMarkers(polygons: LiveData<List<PolygonEntity>>) {
        polygons.observe(this) { polygonEntities ->
            polygonEntities.forEach { polygonEntity ->
                val centerLatLng = stringToLatLng(polygonEntity.centerPoint)
                val markerOptions = MarkerOptions()
                    .position(centerLatLng)
                    .title("Area: ${polygonEntity.area}")
                googleMap.addMarker(markerOptions)
            }
        }
    }

    /*This function retrieves all polygons from the database.
    Once the polygons are retrieved,it updates the map with all the
    polygons and adds markers for
    their centers. The updates are executed on the Main thread.
    */
    private fun retrievePolygons() {
        CoroutineScope(Dispatchers.Main).launch {
            val polygons = viewModel.getAll()
            updateMap(polygons)
            addCenterMarkers(polygons)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        getLocation()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.setOnMapClickListener { point ->
            polygonPoints.add(point)
            polygon?.remove()

            val polygonOptions = PolygonOptions()
                .addAll(polygonPoints)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(128, 255, 0, 0))
            polygon = googleMap.addPolygon(polygonOptions)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_button -> finishPolygon()
            R.id.retrieve_button -> retrievePolygons()
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}