package com.yumaoem.feature_home.data.dto.start_diy_swap

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class StartDiySwapRequestDto(

	@SerialName("token_id")
	val tokenId: Int? = null,

	@SerialName("bike_name")
	val bikeName: String? = null,

	@SerialName("client_city_id")
	val clientCityId: Int? = null
)
