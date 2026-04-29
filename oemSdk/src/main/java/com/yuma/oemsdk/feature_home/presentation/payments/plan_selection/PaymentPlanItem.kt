package com.yumaoem.feature_home.presentation.payments.plan_selection

data class PaymentPlanItem(
    val id: Int,
    val title: String,
    val range: String,
    val validity:String,
    val price: String,
    val isSelected:Boolean
)
