package com.yumaoem.feature_home.data.dto.check_in_user

import com.yumaoem.feature_home.domain.model.token_flow.check_in.CheckInResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckInResponseDTO(
    @SerialName("id")
    val id:Int
){
    fun toDomain() = CheckInResponse(
        status = true
    )
}
