package com.yumaoem.feature_home.data.dto.start_diy_swap

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class StartDiySwapResponseDTO(

	@SerialName("data")
	val data: Data? = null,

	@SerialName("message")
	val message: String? = null,

	@SerialName("status")
	val status: Int? = null
)

@Serializable
data class Data(

	@SerialName("bike_group_id")
	val bikeGroupId: Int? = null,

	@SerialName("client_bike_id")
	val clientBikeId: Int? = null,

	@SerialName("bike_group_name")
	val bikeGroupName: String? = null,

	@SerialName("swappable_cnt")
	val swappableCnt: String? = null,

	@SerialName("task_id")
	val taskId: Int? = null,

	@SerialName("is_inside_location")
	val isInsideLocation: Boolean? = null,

	@SerialName("location_id")
	val locationId: Int? = null,

	@SerialName("client_id")
	val clientId: Int? = null,

	@SerialName("client_city_id")
	val clientCityId: Int? = null
)