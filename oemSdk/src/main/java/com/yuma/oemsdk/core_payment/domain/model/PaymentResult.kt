package com.yumacustomer.core_payments.domain.model

sealed class PaymentResult {
    data class Success(
        val orderId: String,
        val paymentId: String? = null,
        val signature: String? = null
    ) : PaymentResult()

    data class Failure(
        val orderId: String,
        val errorCode: String,
        val errorMessage: String,
        val errorDescription: String? = null
    ) : PaymentResult()

    data class Cancelled(val orderId: String) : PaymentResult()
}