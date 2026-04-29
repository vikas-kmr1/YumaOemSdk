package com.yumaoem.feature_home.data.dto.maps_screen.all_stations.request

import kotlinx.serialization.Serializable

@Serializable
data class AllStationsRequest(
    val latitude: Double,
    val longitude: Double,
    val clientId:Int,
    val clientCityId:Int,
    val userId:Int,
    val clientVehicleId:Int
)
