package com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel

import kotlinx.serialization.Serializable

@Serializable
data class LatLong(
    val latitude: Double,
    val longitude: Double,
)