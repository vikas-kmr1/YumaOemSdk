package com.yumaoem.feature_home.data.dto.fcm_token.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoveFcmTokenRequestDto(
    @SerialName("user_id")
    val userId: Int,
)
