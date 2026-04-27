package com.yumacustomer.core_payments.domain.model

enum class PaymentEnvironment {
    SANDBOX, PRODUCTION
}

fun getPaymentEnvironmentForFlavour(flavor: String): PaymentEnvironment {
    return when (flavor) {
        "dev" -> PaymentEnvironment.SANDBOX
        "prodReplica", "prod" -> PaymentEnvironment.PRODUCTION
        else -> PaymentEnvironment.PRODUCTION
    }
}