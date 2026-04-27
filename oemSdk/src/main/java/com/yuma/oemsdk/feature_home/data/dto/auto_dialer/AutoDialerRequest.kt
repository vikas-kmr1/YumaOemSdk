package com.yumaoem.feature_home.data.dto.auto_dialer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutoDialerRequest(
    @SerialName("auto_dailer_details")
    val autoDialerDetails: List<AutoDialerDetail>
)

@Serializable
data class AutoDialerDetail(
    @SerialName("client_id")
    val clientId: Int? = null,

    @SerialName("client_user_id")
    val clientUserId: Int? = null,

    @SerialName("client_city_id")
    val clientCityId: Int? = null,

    @SerialName("client_city")
    val clientCity: String? = null,

    @SerialName("user_name")
    val userName: String, // ✅ mandatory

    @SerialName("user_phone_number")
    val userPhoneNumber: String, // ✅ mandatory

    @SerialName("bike_number")
    val bikeNumber: String? = null,

    @SerialName("yuma_client_token_id")
    val yumaClientTokenId: Int? = null,

    @SerialName("charging_station_id")
    val chargingStationId: Int? = null,

    @SerialName("charging_unit_id")
    val chargingUnitId: Int? = null,

    @SerialName("charging_unit_qr_code")
    val chargingUnitQrCode: String? = null,

    @SerialName("battery_count")
    val batteryCount: Int? = null,

    @SerialName("battery_id_1")
    val batteryId1: Int? = null,

    @SerialName("battery_qr_code_1")
    val batteryQrCode1: String? = null,

    @SerialName("battery_item_group_id_1")
    val batteryItemGroupId1: Int? = null,

    @SerialName("battery_id_2")
    val batteryId2: Int? = null,

    @SerialName("battery_qr_code_2")
    val batteryQrCode2: String? = null,

    @SerialName("battery_item_group_id_2")
    val batteryItemGroupId2: Int? = null
)
