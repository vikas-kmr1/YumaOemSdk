package com.yumaoem.feature_onboarding.data.dto.verify_otp.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SilentAuthRequest(

	@SerialName("auth_code")
	val authcode: String,

	@SerialName("model")
	val model: String,

	@SerialName("manufacturer")
	val manufacturer: String,

	@SerialName("osName")
	val osName: String,

	@SerialName("sdkVersion")
	val sdkVersion: String,

	@SerialName("brand")
	val brand: String,

	@SerialName("androidId")
	val androidId: String,

	@SerialName("screenResolution")
	val screenResolution: String,

	@SerialName("deviceType")
	val deviceType: String
)

