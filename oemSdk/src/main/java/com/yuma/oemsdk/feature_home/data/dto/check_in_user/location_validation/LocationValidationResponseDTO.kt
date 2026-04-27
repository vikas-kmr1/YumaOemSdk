package com.yumaoem.feature_home.data.dto.check_in_user.location_validation

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LocationValidationResponseDTO(

	@SerialName("distance")
	val distance: Int,

	@SerialName("nearestChargingStationId")
	val nearestChargingStationId: Int,

	@SerialName("tokenChargingStationId")
	val tokenChargingStationId: String,

	@SerialName("isLocationValid")
	val isLocationValid: Boolean,

	@SerialName("message")
	val message: String
)
