package com.yumaoem.feature_home.data.dto.cancel_token_booking.response

import com.yumaoem.feature_home.domain.model.token_flow.cancel_token_booking.CancelTokenResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CancelTokenBookingResponseDTO(
    @SerialName("status")
    val status:Boolean
){
    fun toDomain() = CancelTokenResponse(
        status = status
    )
}
