package com.yumaoem.core_network.impl.data.base

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T
)
