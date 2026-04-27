package com.yumaoem.feature_home.domain.model.payments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrder(
    @SerialName("plan_id")
    val planId:String? = null,

    @SerialName("order_id")
    val orderId:String,

    @SerialName("payment_session_id")
    val sessionId:String,
)
