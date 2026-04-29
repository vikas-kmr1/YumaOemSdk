package com.yumaoem.feature_home.common.util.map_utils

import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong


fun decodePolyline(encoded: String?): List<LatLong> {
    if (encoded == null) return emptyList()

    val poly = mutableListOf<LatLong>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        poly.add(LatLong(lat / 1E5, lng / 1E5))
    }

    return poly
}