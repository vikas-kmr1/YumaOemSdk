package com.yumaoem.feature_home.data.dto.book_token.response

import com.yumaoem.feature_home.data.dto.station_operation_status.response.StationOperationStatusResponseDTO
import com.yumaoem.feature_home.data.dto.station_operation_status.response.toYumaStationMarker
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class BookTokenResponseDTO(
	@SerialName("isDiyToken")
	val isDiyToken: Boolean,

	@SerialName("latitude")
	val latitude: Double,

	@SerialName("longitude")
	val longitude: Double,

	@SerialName("clientVehicleQrCode")
	val clientVehicleQrCode: String,

	@SerialName("tokenNumber")
	val tokenNumber: String,

	@SerialName("tokenStatusId")
	val tokenStatusId: Int,

	@SerialName("tokenId")
	val tokenId: String,

	@SerialName("chargingStationId")
	val chargingStationId: String,

	@SerialName("tokenExpiryTimeStamp")
	val tokenExpiryTimeStamp: Long,

	@SerialName("tokenBookingTime")
	val tokenBookingTime: Long,

	@SerialName("batteryCount")
	val batteryCount: Int,

	@SerialName("batteryType")
	val batteryType: Int,

	@SerialName("stationStatus")
	val bookingStation: StationOperationStatusResponseDTO? = null,

	@SerialName("isNewDiyUser")
	val isNewDiyUser: Boolean? = null
) {
	fun toDomain() = BookedTokenDetails(
		isDiyToken = isDiyToken,
		latitude = latitude,
		clientVehicleQrCode = clientVehicleQrCode,
		tokenNumber = tokenNumber,
		tokenStatusId = tokenStatusId,
		tokenID = tokenId,
		chargingStationId = chargingStationId,
		tokenExpiryTimeStamp = tokenExpiryTimeStamp,
		tokenBookingTimeStamp = tokenBookingTime,
		longitude = longitude,
		batteryType = batteryType,
		batteryCount = batteryCount,
		isNewDiyUser = isNewDiyUser?:false,
		bookingStation = bookingStation?.toYumaStationMarker()
	)
}
