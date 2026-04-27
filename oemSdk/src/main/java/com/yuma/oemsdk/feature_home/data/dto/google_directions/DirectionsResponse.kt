package com.yumaoem.feature_home.data.dto.google_directions

import kotlinx.serialization.Serializable

@Serializable
data class DirectionsResponse(
    val routes: List<Route>
)

@Serializable
data class Route(
    val overview_polyline: OverviewPolyline
)

@Serializable
data class OverviewPolyline(
    val points: String
)
