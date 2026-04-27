package com.yumacustomer.core_payments.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentTheme(
    val primaryColor: String = "#6A3FD3",
    val backgroundColor: String = "#FFFFFF",
    val buttonTextColor: String = "#FFFFFF",
    val primaryTextColor: String = "#000000",
    val secondaryTextColor: String = "#666666"
)