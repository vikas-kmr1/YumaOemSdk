package com.yumaoem.core_network.impl.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RefreshTokenRequestBody(
	@SerialName("refreshToken")
	val refreshToken: String
)
