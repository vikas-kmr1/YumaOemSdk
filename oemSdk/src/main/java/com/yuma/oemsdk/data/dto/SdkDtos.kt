package com.yuma.oemsdk.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── User / Auth ───────────────────────────────────────────────────────────────

@Serializable
data class SdkUserDto(
    @SerialName("userId")        val userId: String? = null,
    @SerialName("firstName")     val firstName: String? = null,
    @SerialName("surname")       val surname: String? = null,
    @SerialName("phone")         val phone: String? = null,
    @SerialName("bikeProvider")  val bikeProvider: String? = null,
    @SerialName("bikeNumber")    val bikeNumber: String? = null,
    @SerialName("clientVehicleId")      val clientVehicleId: Int? = null,
    @SerialName("clientVehicleQrCode")  val clientVehicleQrCode: String? = null,
    @SerialName("clientCityId")  val clientCityId: Int? = null,
    @SerialName("isB2c")         val isB2c: Boolean? = null
)

@Serializable
data class SdkLogoutRequestDto(
    @SerialName("refreshToken") val refreshToken: String
)

@Serializable
data class SdkRemoveFcmTokenDto(
    @SerialName("userId") val userId: Int
)

// ── Swap History ──────────────────────────────────────────────────────────────

@Serializable
data class SdkSwapHistoryRequestDto(
    @SerialName("clientVehicleId") val clientVehicleId: Int,
    @SerialName("page")            val page: Int,
    @SerialName("limit")           val limit: Int
)

@Serializable
data class SdkSwapHistoryResponseDto(
    @SerialName("data") val data: List<SdkSwapDayDto>? = null
)

@Serializable
data class SdkSwapDayDto(
    @SerialName("date")  val date: String = "",
    @SerialName("swaps") val swaps: List<SdkSwapItemDto> = emptyList()
)

@Serializable
data class SdkSwapItemDto(
    @SerialName("swapTime")    val swapTime: String = "",
    @SerialName("serviceTime") val serviceTime: String = ""
)

// ── Battery Details ───────────────────────────────────────────────────────────

@Serializable
data class SdkBatteryDetailsDto(
    @SerialName("batteryId")  val batteryId: Int = 0,
    @SerialName("batteryQr")  val batteryQr: String = ""
)

// ── Nearby Stations ───────────────────────────────────────────────────────────

@Serializable
data class SdkAllStationsRequestDto(
    val latitude: Double,
    val longitude: Double,
    val clientCityId: Int,
    val clientVehicleId: Int,
    val userId: String
)

@Serializable
data class SdkNearbyStationsResponseDto(
    @SerialName("data") val data: List<SdkStationDto>? = null
)

@Serializable
data class SdkStationDto(
    @SerialName("csId")        val csId: Int = 0,
    @SerialName("name")        val name: String = "",
    @SerialName("latitude")    val latitude: Double = 0.0,
    @SerialName("longitude")   val longitude: Double = 0.0,
    @SerialName("distance")    val distance: Double? = null,
    @SerialName("availableTokens") val availableTokens: Int? = null,
    @SerialName("operationStatus") val operationStatus: String? = null
)

// ── Token Booking ─────────────────────────────────────────────────────────────

@Serializable
data class SdkBookTokenRequestDto(
    @SerialName("csId")            val csId: Int,
    @SerialName("clientUserId")    val clientUserId: String,
    @SerialName("clientVehicleId") val clientVehicleId: Int
)

@Serializable
data class SdkBookTokenResponseDto(
    @SerialName("tokenId")    val tokenId: Int? = null,
    @SerialName("qrCode")     val qrCode: String? = null,
    @SerialName("expiryTime") val expiryTime: String? = null
)

// ── Token Status ──────────────────────────────────────────────────────────────

@Serializable
data class SdkTokenStatusResponseDto(
    @SerialName("status")  val status: String? = null,
    @SerialName("tokenId") val tokenId: Int? = null
)

// ── Payment Plans ─────────────────────────────────────────────────────────────

@Serializable
data class SdkPaymentPlansResponseDto(
    @SerialName("data") val data: List<SdkPlanDto>? = null
)

@Serializable
data class SdkPlanDto(
    @SerialName("planId")       val planId: Int = 0,
    @SerialName("planName")     val planName: String = "",
    @SerialName("price")        val price: Double = 0.0,
    @SerialName("description")  val description: String = "",
    @SerialName("swapLimit")    val swapLimit: Int? = null,
    @SerialName("validityDays") val validityDays: Int? = null
)

@Serializable
data class SdkCreateOrderRequestDto(
    @SerialName("planId")          val planId: Int,
    @SerialName("clientVehicleId") val clientVehicleId: String
)

@Serializable
data class SdkCreateOrderResponseDto(
    @SerialName("orderId")    val orderId: String? = null,
    @SerialName("cfOrderId")  val cfOrderId: String? = null,
    @SerialName("paymentSessionId") val paymentSessionId: String? = null
)

// ── Support ───────────────────────────────────────────────────────────────────

@Serializable
data class SdkSupportDetailsDto(
    @SerialName("phoneNumber")      val phoneNumber: Long? = null,
    @SerialName("defaultMessage")   val defaultMessage: String? = null
)

// ── Generic ───────────────────────────────────────────────────────────────────

@Serializable
data class SdkGenericSuccessDto(
    @SerialName("message") val message: String? = null,
    @SerialName("success") val success: Boolean? = null
)
