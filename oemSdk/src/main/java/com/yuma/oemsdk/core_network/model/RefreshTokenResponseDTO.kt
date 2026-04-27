package com.yumaoem.core_network.impl.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RefreshTokenResponseDTO(

	@SerialName("accessToken")
	val accessToken: AccessToken,

	@SerialName("refreshToken")
	val refreshToken: RefreshToken
)

@Serializable
data class User(

	@SerialName("firstName")
	val firstName: String,

	@SerialName("phone")
	val phone: String,

	@SerialName("lastTokenIssueDt")
	val lastTokenIssueDt: Int,

	@SerialName("surname")
	val surname: String,

	@SerialName("hashedPassword")
	val hashedPassword: String,

	@SerialName("id")
	val id: String,

	@SerialName("userStatusId")
	val userStatusId: Int,

	@SerialName("email")
	val email: String
)

@Serializable
data class AccessToken(

	@SerialName("expiresIn")
	val expiresIn: String,

	@SerialName("token")
	val token: String
)

@Serializable
data class RefreshToken(

	@SerialName("expiresIn")
	val expiresIn: String,

	@SerialName("token")
	val token: String
)
