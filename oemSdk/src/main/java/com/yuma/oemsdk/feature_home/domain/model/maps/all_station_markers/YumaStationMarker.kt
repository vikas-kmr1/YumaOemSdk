package com.yumaoem.feature_home.domain.model.maps.all_station_markers

import com.yumaoem.core.utils.currentTimeMillis
import com.yumaoem.feature_home.domain.model.maps.stationRoute.RouteInfo
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.ChargingStationState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong

data class YumaStationMarker(
    val stationName:String,
    val stationId:Int,
    val title:String? = null,
    val location: LatLong,
    val numberOfViewsForStation:Int = 1,
    val directionsViewCount:Int = 0,
    val stationCurrentStatus: YumaStationStatus = YumaStationStatus()
)

data class YumaStationStatus(
    val stationStatusId: Int = 5,
    val stationState: ChargingStationState = ChargingStationState.NOT_AVAILABLE,
    val currentTime: Long = currentTimeMillis(),
    val nextOpeningTime: Int? = null,
    val nextClosingTime: Int? = null,
    val distanceFromUser: String = "",
    val distanceInMeters: Double = 0.0,
    val travelDuration:String = "",
    val isAmongNearestStations: Boolean = false,
    val routeData: RouteInfo? = null
)
