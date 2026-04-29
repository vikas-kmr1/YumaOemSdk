package com.yumacustomer.core_payments.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentSession(
    val orderId: String,
    val paymentSessionId: String,
    val orderToken: String? = null
)