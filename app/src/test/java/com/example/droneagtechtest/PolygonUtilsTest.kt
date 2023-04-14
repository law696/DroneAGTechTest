package com.example.droneagtechtest

import com.example.droneagtechtest.utils.PolygonUtils
import com.google.android.gms.maps.model.LatLng
import junit.framework.TestCase.assertEquals
import org.junit.Test

class PolygonUtilsTest {

    @Test
    fun testCalculatePolygonCenter() {
        val points = listOf(
            LatLng(37.4219999,-122.0840575),
            LatLng(37.4219499,-122.0841405),
            LatLng(37.4217666,-122.0841405),
            LatLng(37.4217666,-122.0838851),
            LatLng(37.4218499,-122.0838647),
            LatLng(37.4219999,-122.0839424)
        )

        val expectedCenter = LatLng(37.421883, -122.084017)
        val actualCenter = PolygonUtils.calculatePolygonCenter(points)

        assertEquals(expectedCenter.latitude, actualCenter.latitude, 0.0001)
        assertEquals(expectedCenter.longitude, actualCenter.longitude, 0.0001)
    }

    @Test
    fun testCalculatePolygonArea() {
        val points = listOf(
            LatLng(37.4219999,-122.0840575),
            LatLng(37.4219499,-122.0841405),
            LatLng(37.4217666,-122.0841405),
            LatLng(37.4217666,-122.0838851),
            LatLng(37.4218499,-122.0838647),
            LatLng(37.4219999,-122.0839424)
        )

        val expectedArea = "545.89 m²"
        val actualArea = PolygonUtils.calculatePolygonArea(points)

        assertEquals(expectedArea, actualArea)
    }
}