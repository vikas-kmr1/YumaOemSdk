package com.yumacustomer.core_payments.domain.paymentContextProvider

import androidx.activity.ComponentActivity

/**
 * Platform-specific context provider for payment operations
 * This abstraction allows the core module to request context when needed
 */
interface PaymentContextProvider {
    suspend fun providePaymentContext(): PaymentContext
}

/**
 * Platform-specific payment context
 */
class PaymentContext(
    val activity: ComponentActivity
)