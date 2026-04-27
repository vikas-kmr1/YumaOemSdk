package com.yumaoem.feature_home.domain.model.maps.nearby_stations

import kotlinx.serialization.Serializable

@Serializable
data class NearbyChargingStations(
    val maxAllowedRadius: Int? = null,
    val defaultStation: StationsItem? = null,
    val totalStations: Int? = null,
    val breakStations: Int? = null,
    val closedStations: Int? = null,
    val stations: List<StationsItem?>? = null,
    val openStations: Int? = null,
    val searchRadius: Int? = null
)

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class StationsItem(
    val csName: String,
    val csId: Int,
    val coordinates: Coordinates,
    val distance: Double? = null,
    val operationStatus: OperationStatus? = null,
)

@Serializable
data class OperationStatus(
    val dayOfWeek: Int? = null,
    val currentStatus: CurrentStatus? = null,
    val operationHours: List<OperationHoursItem?>? = null,
    val breakHours: List<String?>? = null,
    val stationId: Int? = null
)

@Serializable
data class CurrentStatus(
    val currentTime: Int? = null,
    val nextClosingTimeIST: String? = null,
    val statusText: String? = null,
    val nextOpeningTime: String? = null,
    val nextClosingTime: String? = null,
    val isStationOpen: Boolean? = null,
    val nextOpeningTimeIST: String? = null,
    val currentTimeIST: String? = null
)

@Serializable
data class OperationHoursItem(
    val startTimeIST: String? = null,
    val startTime: Int? = null,
    val endTime: Int? = null,
    val endTimeIST: String? = null
)
