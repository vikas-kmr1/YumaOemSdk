package com.yumaoem.feature_home.presentation.payments.plan_selection.event

import com.yumaoem.feature_home.presentation.payments.plan_selection.PaymentPlanItem

sealed class FirstPlanPaymentEvent {
    data class OnTabChanged(val planTypeId: Int) : FirstPlanPaymentEvent()
    data class PlanSelected(val plan: PaymentPlanItem) : FirstPlanPaymentEvent()
    object ProceedClicked : FirstPlanPaymentEvent()
}