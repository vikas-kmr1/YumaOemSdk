package com.yumaoem.core_network.impl.model

import kotlinx.serialization.Serializable

@Serializable
data class YuzenErrorResponseDTO(
    val status: Int,
    val message: String
)