package com.yumaoem.feature_onboarding.domain.model.verify_otp.response

data class SilentAuthResponse(
    val accessToken: Token,
    val refreshToken: Token,
    val orderId: Int,
    val user: User
)

data class Token(
    val expiresIn: String,
    val token: String
)

data class User(
    val id: String,
    val phone: String,
    val firstName: String,
    val surname: String,
    val isPrePaidUser: Boolean,
    val userProfileUrl: String? = null,
    val currentClientCityIds: List<CurrentClientCityIds>,
    val clientDetails: List<ClientDetails>,
    val clientVehicles: List<ClientVehicles>,
    val activeClientUsers: List<ActiveClientUsers>,
    val batteryCount:Int
)

data class ClientVehicles(
    val itemGroupId: Int,
    val qrCode: String,
    val clientId: Int,
    val clientCityId: Int,
    val clientVehicleId: Int,
    val bikeProvider: String,
    val bikeNumber: String
)

data class ClientDetails(
    val organisationName: String,
    val clientId: Int
)


data class CurrentClientCityIds(
    val clientId: Int,
    val clientCityId: Int
)

data class ActiveClientUsers(
    val clientUserId: Int,
    val clientId: Int
)
