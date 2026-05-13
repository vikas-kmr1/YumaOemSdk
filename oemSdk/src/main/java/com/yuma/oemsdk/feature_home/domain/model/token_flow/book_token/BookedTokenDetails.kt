package com.yumaoem.feature_home.domain.model.token_flow.book_token

import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker


data class BookedTokenDetails(
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
    val bookingStation: YumaStationMarker?,
    val isNewDiyUser:Boolean,
    val isBatteryVerificationRequired: Boolean,
    val isBatteryVerified: Boolean
)
