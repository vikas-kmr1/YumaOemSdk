package com.yumaoem.feature_home.data.dto.station_directions.request

import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong

data class RouteInfoRequest(
    val origin:LatLong,
    val destination:LatLong,
)
