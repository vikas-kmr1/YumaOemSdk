package com.yumaoem.core.utils.map_style

import android.location.Location

fun calculateDistanceInMeters(
    startLat: Double,
    startLng: Double,
    endLat: Double,
    endLng: Double
): Double {
    val result = FloatArray(1)
    Location.distanceBetween(startLat, startLng, endLat, endLng, result)
    return result[0].toDouble()
}
