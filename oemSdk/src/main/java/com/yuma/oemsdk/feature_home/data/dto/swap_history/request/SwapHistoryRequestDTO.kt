package com.yumaoem.feature_home.data.dto.swap_history.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SwapHistoryRequest(

    @SerialName("clientVehicleId")
    val clientVehicleId: Int,

    @SerialName("limit")
    val limit: Int,

    @SerialName("page")
    val page: Int
)