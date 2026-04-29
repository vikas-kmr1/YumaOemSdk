package com.yumaoem.feature_home.data.dto.beacon_details.request

import kotlinx.serialization.Serializable

@Serializable
data class BeaconDetailsRequest(
    val latitude: Double,
    val longitude: Double,
    val tokenId:Int
)
