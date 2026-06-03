package com.yumaoem.feature_home.data.dto.verify_batteries.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyBatteriesDTO (
    @SerialName("client_vehicle_id")
    val clientVehicleId: Int,

    @SerialName("scanned_battery_qr_codes")
    val scannedBatteryQrCodes: List<String>,

    @SerialName("token_id")
    val tokenId: Long
)