package com.yumaoem.feature_home.presentation.payments.navigation

import kotlinx.serialization.Serializable

@Serializable
object PaymentPlansScreen

@Serializable
data class PaymentDetailsScreen(val planJson: String)

@Serializable
object PaymentSuccessScreen

@Serializable
object PaymentHomeScreen