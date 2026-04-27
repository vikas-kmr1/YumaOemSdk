package com.yumaoem.feature_home.domain.model.payments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatus(
    @SerialName("order_id")
    val orderId:String? = null,

    @SerialName("payment_status")
    val paymentStatus:String,
)
