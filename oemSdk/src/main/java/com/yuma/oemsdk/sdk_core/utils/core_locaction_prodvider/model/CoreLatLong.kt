package com.yumaoem.core.utils.core_locaction_prodvider.model

import kotlinx.serialization.Serializable

@Serializable
data class CoreLatLong(
    val latitude: Double,
    val longitude: Double,
)