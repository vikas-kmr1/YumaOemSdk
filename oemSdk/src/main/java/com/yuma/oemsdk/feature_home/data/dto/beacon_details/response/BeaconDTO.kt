package com.yumaoem.feature_home.data.dto.beacon_details.response

import com.yumaoem.feature_home.domain.model.token_flow.beacon_details.Beacon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeaconDTO(

	@SerialName("macId")
	val macId: String,

	@SerialName("uuid")
	val uuid: String,
	
	@SerialName("chargingStationId")
	val chargingStationId: Int? = null,
) {
	fun toDomain(): Beacon = Beacon(
		macId = macId.uppercase(),
		uuid = uuid.uppercase()
	)
}
