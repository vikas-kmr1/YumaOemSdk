package com.yumaoem.feature_onboarding.data.dto.fcm_token.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertFcmRequest(
    @SerialName("user_id")
    val userId: Int,

    @SerialName("fcmToken")
    val fcmToken: String,

    val authToken: String
)
