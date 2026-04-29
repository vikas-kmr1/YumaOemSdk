package com.yumacustomer.core_payments.domain

import com.yumacustomer.core_payments.domain.model.PaymentEnvironment
import com.yumacustomer.core_payments.domain.model.PaymentMode
import com.yumacustomer.core_payments.domain.model.PaymentSession
import com.yumacustomer.core_payments.domain.model.PaymentTheme
import com.yumacustomer.core_payments.domain.paymentContextProvider.PaymentContext

interface PaymentGateway {
    suspend fun initiatePayment(
        session: PaymentSession,
        environment: PaymentEnvironment = PaymentEnvironment.PRODUCTION,
        mode: PaymentMode = PaymentMode.UPI_INTENT,
        theme: PaymentTheme = PaymentTheme(),
        context: PaymentContext,
    ): Result<Unit>

    fun isUPIAppAvailable(packageName: String): Boolean
    fun getAvailableUPIApps(): List<String>
}