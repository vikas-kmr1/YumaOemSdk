package com.yumaoem.feature_home.data.dto.beacon_details.response

import com.yumaoem.feature_home.domain.model.token_flow.beacon_details.Beacon
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class BeaconResponseDTO(

	@SerialName("macIDs")
	val macIDs: List<String?>,

	@SerialName("uuIDs")
	val uuIDs: List<String?>
) {
	fun toDomain(): Beacon = Beacon(
		macId = macIDs.first().orEmpty().uppercase(),
		uuid = uuIDs.first().orEmpty().uppercase()
	)
}