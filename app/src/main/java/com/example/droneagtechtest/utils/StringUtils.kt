package com.example.droneagtechtest.utils

import com.google.android.gms.maps.model.LatLng

object StringUtils {

    /*
     This function converts a LatLng object to a string in the format
    of "latitude,longitude;" where latitude and longitude are the
     coordinates of the given point. It then returns the resulting string.
     */
    fun latLngToString(point: LatLng): String {
        val sb = StringBuilder()
        sb.append(point.latitude)
        sb.append(",")
        sb.append(point.longitude)
        sb.append(";")

        return sb.toString().dropLast(1)
    }

    /*
    This function takes in a list of LatLng objects and returns a string
    representation of those points. Each point is separated by a semicolon
    and its latitude and longitude are separated by a comma.
    The last semicolon is dropped from the string before returning it.
     */
    fun listToString(points: List<LatLng>): String {
        val sb = StringBuilder()
        for (point in points) {
            sb.append(point.latitude)
            sb.append(",")
            sb.append(point.longitude)
            sb.append(";")
        }
        return sb.toString().dropLast(1)
    }

    /*
    This function takes in a string representation of a latitude-longitude
    coordinate (in the format "latitude,longitude") and returns a LatLng object.
    It first splits the string at the comma, then converts the latitude
    and longitude values from string to double, and finally creates
    a new LatLng object using those values.
     */
    fun stringToLatLng(str: String): LatLng {
        val latLng: LatLng
        val values = str.split(",")
        val lat = values[0].toDouble()
        val lng = values[1].toDouble()
        latLng = LatLng(lat, lng)
        return latLng
    }

    /*
    This function takes in a string that represents a list of points in
    latitude and longitude format, separated by semicolons.
    It then converts the string into a list of LatLng objects and returns it.
     */
    fun stringToList(str: String): List<LatLng> {
        val points = mutableListOf<LatLng>()
        val tokens = str.split(";")
        for (token in tokens) {
            val values = token.split(",")
            val lat = values[0].toDouble()
            val lng = values[1].toDouble()
            points.add(LatLng(lat, lng))
        }
        return points
    }
}