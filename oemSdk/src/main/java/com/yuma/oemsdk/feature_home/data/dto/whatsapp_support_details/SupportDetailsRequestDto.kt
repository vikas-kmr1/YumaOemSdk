package com.yumaoem.feature_home.data.dto.whatsapp_support_details

import kotlinx.serialization.SerialName

data class SupportDetailsRequestDto(
    @SerialName("clientUserId")
    val clientUserId: Int,
    @SerialName("clientVehicleId")
    val clientVehicleId: Int,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double
)
