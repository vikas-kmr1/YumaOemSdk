package com.yumaoem.corepreference.model

import kotlinx.serialization.Serializable

@Serializable
data class BookedTokenDetailsDTO(
    val isDiyToken: Boolean,
    val latitude: Double,
    val clientVehicleQrCode: String,
    val tokenNumber: String,
    val tokenStatusId: Int,
    val tokenID: String,
    val chargingStationId: String,
    val tokenExpiryTimeStamp: Long,
    val tokenBookingTimeStamp: Long,
    val longitude: Double,
    val batteryType:Int,
    val batteryCount:Int,
    val isNewDiyUser:Boolean = false,
    val bookingStation: YumaStationMarkerDTO? = null,
    val isBatteryVerificationRequired: Boolean = false,
    val isBatteryVerified: Boolean = false
)

@Serializable
data class YumaStationMarkerDTO(
    val stationName: String,
    val stationId: Int,
    val title:String? = null,
    val location: LatLongDTO,
    val stationCurrentStatus: YumaStationStatusDTO
)

@Serializable
data class YumaStationStatusDTO(
    val stationStatusId: Int,
    val stationState: String, // Serialized enum name
    val currentTime: Long,
    val nextOpeningTime: Int? = null,
    val nextClosingTime: Int? = null,
    val distanceFromUser: String = ""
)

@Serializable
data class LatLongDTO(
    val latitude: Double,
    val longitude: Double,
)
