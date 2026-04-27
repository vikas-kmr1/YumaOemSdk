package com.yumaoem.feature_home.data.dto.check_in_user.location_validation

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LocationValidationRequestDTO(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("tokenId")
    val tokenId: Int
) 