package com.yumaoem.feature_home.data.dto.payment_plans.payment_status

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusRequestDTO(
    @SerialName("order_id")
    val orderId:String,

    @SerialName("payment_session_id")
    val paymentSessionId:String,
)
