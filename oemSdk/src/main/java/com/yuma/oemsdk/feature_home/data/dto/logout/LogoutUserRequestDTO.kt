package com.yumaoem.feature_home.data.dto.logout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutUserRequestDTO(
    @SerialName("refresh_token")
    val refreshToken: String
)
