package com.yumaoem.core_network.impl.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class YumaErrorResponseDTO(
    val statusCode: Int,
    val message: String,
    val data: JsonElement? = null
)
