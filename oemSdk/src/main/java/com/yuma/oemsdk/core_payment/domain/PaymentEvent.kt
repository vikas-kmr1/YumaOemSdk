package com.yumacustomer.core_payments.domain

import com.yumacustomer.core_payments.domain.model.PaymentResult

sealed class PaymentEvent {
    data class Success(val result: PaymentResult.Success) : PaymentEvent()
    data class Failure(val result: PaymentResult.Failure) : PaymentEvent()
    data class Cancelled(val result: PaymentResult.Cancelled) : PaymentEvent()
}