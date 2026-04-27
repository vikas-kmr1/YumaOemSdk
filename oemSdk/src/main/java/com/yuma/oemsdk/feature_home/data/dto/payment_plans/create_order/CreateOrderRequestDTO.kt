package com.yumaoem.feature_home.data.dto.payment_plans.create_order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreateOrderRequestDTO(
    @SerialName("plan_id")
    val planId: String,

    @SerialName("client_vehicle_id")
    val clientVehicleId: Int,
)
