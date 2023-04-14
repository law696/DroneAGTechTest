package com.example.droneagtechtest.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

object PolygonUtils {

    /*
    This function calculates the center point of a polygon
    represented as a list of LatLng points. It computes the average
    latitude and longitude of all the points to get the center point.
  */
    fun calculatePolygonCenter(points: List<LatLng>): LatLng {
        var latSum = 0.0
        var lngSum = 0.0

        for (point in points) {
            latSum += point.latitude
            lngSum += point.longitude
        }

        val centerLat = latSum / points.size
        val centerLng = lngSum / points.size

        return LatLng(centerLat, centerLng)
    }

    /*
    This function calculates the area of a polygon represented as a
    list of LatLng points. It uses the SphericalUtil class from the
    Google Maps Android API utility library to compute the area.
     The computed area is returned as a string formatted as "X m²",
     where X is the area value rounded to two decimal places.
   */
    fun calculatePolygonArea(points: List<LatLng>): String {
        return String.format("%.2f m²", SphericalUtil.computeArea(points))
    }
}