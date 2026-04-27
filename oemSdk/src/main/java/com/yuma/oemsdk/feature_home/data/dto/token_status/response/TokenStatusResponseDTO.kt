package com.yumaoem.feature_home.data.dto.token_status.response

import com.yumaoem.core.utils.orZero
import com.yumaoem.feature_home.domain.model.token_flow.token_status.TokenStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class TokenStatusResponseDTO(
	@SerialName("token_status_id")
	val tokenStatusId: Int,

	@SerialName("totaltimetaken")
	val totalTimeTaken: String? = null
)
fun TokenStatusResponseDTO.toDomain(): TokenStatus {
	return TokenStatus(
		tokenStatusId = tokenStatusId,
		swapTime = totalTimeTaken.orEmpty()
	)
}
