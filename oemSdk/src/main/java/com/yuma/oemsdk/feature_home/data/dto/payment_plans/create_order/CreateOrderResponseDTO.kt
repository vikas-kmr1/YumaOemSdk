package com.yumaoem.feature_home.data.dto.payment_plans.create_order

import com.yumaoem.feature_home.domain.model.payments.CreateOrder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreateOrderResponseDTO(
    @SerialName("plan_id")
    val planId:String? = null,

    @SerialName("order_id")
    val orderId:String,

    @SerialName("payment_session_id")
    val sessionId:String,
) {
    fun toDomain(): CreateOrder = CreateOrder(
        planId = planId,
        orderId = orderId,
        sessionId = sessionId
    )
}
