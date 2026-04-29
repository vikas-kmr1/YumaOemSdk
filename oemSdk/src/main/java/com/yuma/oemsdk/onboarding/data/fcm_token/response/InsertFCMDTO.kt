package com.yumaoem.feature_onboarding.data.dto.fcm_token.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class InsertFCMDTO(

	@SerialName("message")
	val message: String? = null,

	@SerialName("status")
	val status: Int? = null
)
