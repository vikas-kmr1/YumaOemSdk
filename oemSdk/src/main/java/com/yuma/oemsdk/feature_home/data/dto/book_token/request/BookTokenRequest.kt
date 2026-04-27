package com.yumaoem.feature_home.data.dto.book_token.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class BookTokenRequest(
	val userId: Int,

	@SerialName("clientUserId")
	val clientUserId: Int,

	@SerialName("clientVehicleId")
	val clientVehicleId: Int,

	@SerialName("clientVehicleQrCode")
	val clientVehicleQrCode: String,

	@SerialName("chargingStationId")
	val chargingStationId: Int,

	@SerialName("latitude")
	val latitude: Double,

	@SerialName("longitude")
	val longitude: Double,

	@SerialName("distanceFromChargingStation")
	val distanceFromChargingStation: Int? = null,

	@SerialName("tokenStatusId")
	val tokenStatusId: Int,

	@SerialName("isDiyToken")
	val isDiyToken: Boolean,

	@SerialName("clientCityId")
	val clientCityId: Int,

	@SerialName("clientId")
	val clientId: Int,

	@SerialName("vehicleItemGroupId")
	val vehicleItemGroupId: Int
)
