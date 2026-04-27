package com.yumaoem.feature_home.data.dto.tag_battery.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagBatteryRequestDTO(

    @SerialName("vehicle_qr_code")
    val vehicleQrCode: String,

    @SerialName("client_vehicle_id")
    val clientVehicleId: Int,

    @SerialName("current_battery_qrcodes")
    val currentBatteryQrcodes: List<String>,

    @SerialName("user_id")
    val userId: Int,

    @SerialName("client_user_id")
    val clientUserId: Int,

    @SerialName("battery_count")
    val batteryCount: Int,

    @SerialName("current_latitude")
    val currentLatitude: Double,

    @SerialName("current_longitude")
    val currentLongitude: Double
)
