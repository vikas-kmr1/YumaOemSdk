package com.yumaoem.feature_home.data.dto.auto_dialer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutoDialerResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String
)
