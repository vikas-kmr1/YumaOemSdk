package com.yumaoem.feature_onboarding.data.dto.verify_otp.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SilentAuthResponseDTO(

	@SerialName("accessToken")
	val accessToken: AccessToken,

	@SerialName("user")
	val userInfo: UserInfoDTO,

	@SerialName("refreshToken")
	val refreshToken: RefreshToken
)

@Serializable
data class AccessToken(

	@SerialName("expiresIn")
	val expiresIn: String,

	@SerialName("token")
	val token: String
)

@Serializable
data class RefreshToken(

	@SerialName("expiresIn")
	val expiresIn: String,

	@SerialName("token")
	val token: String
)

@Serializable
data class UserInfoDTO(

	@SerialName("firstName")
	val firstName: String,

	@SerialName("clientDetails")
	val clientDetails: List<ClientDetailsItem>,

	@SerialName("phone")
	val phone: String,

	@SerialName("surname")
	val surname: String? = null,

	@SerialName("currentClientCityIds")
	val currentClientCityIds: List<CurrentClientCityIdsItem>,

	@SerialName("id")
	val userId: String,

	@SerialName("userStatusId")
	val userStatusId: Int,

	@SerialName("clientVehicles")
	val clientVehicles: List<ClientVehiclesItem>,

	@SerialName("activeClientUsers")
	val activeClientUsers: List<ActiveClientUsersDTO>,

	@SerialName("is_prepaid_user")
	val isPrePaidUser: Boolean,

    @SerialName("battery_count")
    val batteryCount: Int,
)

@Serializable
data class ClientVehiclesItem(

	@SerialName("item_group_id")
	val itemGroupId: Int,

	@SerialName("qr_code")
	val qrCode: String,

	@SerialName("client_id")
	val clientId: Int,

	@SerialName("client_city_id")
	val clientCityId: Int,

	@SerialName("client_vehicle_id")
	val clientVehicleId: Int,

	@SerialName("bike_provider")
	val bikeProvider: String,

	@SerialName("bike_number")
	val bikeNumber: String
)

@Serializable
data class CurrentClientCityIdsItem(

	@SerialName("clientId")
	val clientId: Int,

	@SerialName("clientCityId")
	val clientCityId: Int
)

@Serializable
data class ClientDetailsItem(

	@SerialName("organisation_name")
	val organisationName: String,

	@SerialName("client_id")
	val clientId: Int
)

@Serializable
data class ActiveClientUsersDTO(
	@SerialName("client_user_id")
	val clientUserId: Int,

	@SerialName("client_id")
	val clientId: Int,
)




