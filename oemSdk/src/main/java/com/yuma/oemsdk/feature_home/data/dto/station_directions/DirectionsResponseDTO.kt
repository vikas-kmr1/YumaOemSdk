package com.yumaoem.feature_home.data.dto.station_directions

import com.yumaoem.feature_home.domain.model.maps.stationRoute.RouteInfo
import com.yumaoem.feature_home.common.util.map_utils.decodePolyline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectionsResponseDTO(
    @SerialName("routes") val routes: List<RouteDTO>
)

@Serializable
data class RouteDTO(
    @SerialName("legs") val legs: List<LegDTO>,
    @SerialName("overview_polyline") val overviewPolyline: PolylineDTO
)

@Serializable
data class PolylineDTO(
    @SerialName("points") val points: String
)

@Serializable
data class LegDTO(
    @SerialName("distance") val distance: DistanceDTO,
    @SerialName("duration") val duration: DurationDTO,
    @SerialName("start_location") val startLocation: LatLngDTO,
    @SerialName("end_location") val endLocation: LatLngDTO
)

@Serializable
data class DistanceDTO(
    @SerialName("text") val text: String,
    @SerialName("value") val value: Int // in meters
)

@Serializable
data class DurationDTO(
    @SerialName("text") val text: String,
    @SerialName("value") val value: Int // in seconds
)

@Serializable
data class LatLngDTO(
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double
)

fun DirectionsResponseDTO.toDomain(): RouteInfo? {
    val firstRoute = routes.firstOrNull() ?: return null
    val firstLeg = firstRoute.legs.firstOrNull() ?: return null

    return RouteInfo(
        distanceInMeters = firstLeg.distance.value.toDouble(),
        durationInSeconds = firstLeg.duration.value,
        polylinePoints = decodePolyline(firstRoute.overviewPolyline.points)
    )
}

