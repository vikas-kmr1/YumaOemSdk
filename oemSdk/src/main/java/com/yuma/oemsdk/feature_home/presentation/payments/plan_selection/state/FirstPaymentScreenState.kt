package com.yumaoem.feature_home.presentation.payments.plan_selection.state

import com.yumaoem.feature_home.presentation.payments.plan_selection.PaymentPlanItem

data class FirstPaymentScreenState(
    val planGroups: List<PaymentPlanGroup> = emptyList(),
    val selectedPlanGroupId: Int? = null,
    val selectedPlan: PaymentPlanItem? = null,
    val isProceedButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val planDetails: List<String> = listOf(
        "1 Swap = 2 Batteries",
        "Your plan will activate at your first swap.",
        "Mentioned kilometres is an estimate\nand may vary based on riding patterns and bike."
    )
) {
    val currentPlans: List<PaymentPlanItem>
        get() = planGroups.firstOrNull { it.planTypeId == selectedPlanGroupId }?.planItems.orEmpty()
}


data class PaymentPlanGroup(
    val planTypeId:Int,
    val planType: String,
    val planItems: List<PaymentPlanItem>
)
