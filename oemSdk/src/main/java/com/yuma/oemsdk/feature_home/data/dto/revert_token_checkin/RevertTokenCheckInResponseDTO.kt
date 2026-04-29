package com.yumaoem.feature_home.data.dto.revert_token_checkin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RevertTokenCheckInResponseDTO(
    @SerialName("expiredTimestamp")
    val expiredTimeStamp: Long? = null,
)