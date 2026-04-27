package com.yumaoem.core.model.auth

import kotlinx.serialization.Serializable

typealias AuthBearerTokens = BearerTokens

@Serializable
data class BearerTokens(
    val accessToken: String,
    val refreshToken: String
)
