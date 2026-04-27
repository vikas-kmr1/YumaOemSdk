package com.yumaoem.feature_home.domain.model.maps.stationRoute

import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong

data class RouteInfo(
    val stationId:Int? = null,
    val distanceInMeters: Double,
    val durationInSeconds: Int,
    val polylinePoints: List<LatLong>
)
