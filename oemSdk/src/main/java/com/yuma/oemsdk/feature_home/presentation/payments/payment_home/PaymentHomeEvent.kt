package com.yumaoem.feature_home.presentation.payments.payment_home

import com.yumaoem.feature_home.domain.model.payments.UiPlan
import com.yumaoem.feature_home.presentation.payments.plan_selection.PaymentPlanItem

sealed class PaymentHomeEvent {
    data class OnTabChanged(val planGroupId: String) : PaymentHomeEvent()
    data class PlanSelected(val plan: UiPlan) : PaymentHomeEvent()
    object ProceedClicked : PaymentHomeEvent()
}