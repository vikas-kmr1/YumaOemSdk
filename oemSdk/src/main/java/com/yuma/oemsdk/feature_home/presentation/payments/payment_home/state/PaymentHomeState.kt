package com.yumaoem.feature_home.presentation.payments.payment_home.state

import com.yumaoem.feature_home.domain.model.payments.PaymentHomeUiHeader
import com.yumaoem.feature_home.domain.model.payments.UiCurrentPlan
import com.yumaoem.feature_home.domain.model.payments.UiPlan
import com.yumaoem.feature_home.domain.model.payments.UiPlanGroup
import com.yumaoem.feature_home.domain.model.payments.UiText

data class PaymentHomeState(
    val header: PaymentHomeUiHeader,
    val currentPlanDetails: UiCurrentPlan?,
    val plansSectionHeading: UiText,
    val planGroups: List<UiPlanGroup>,
    val selectedPlan: UiPlan? = null,
    val selectedPlanGroupId: String? = null,
    val isLoading: Boolean = false,
    val footerNotes: List<String> = listOf (""),
    val canBuyPlan: Boolean = false
) {
    val currentPlans: List<UiPlan>
        get() = planGroups.firstOrNull { it.groupName == selectedPlanGroupId }?.plans.orEmpty()

    val isProceedButtonEnabled: Boolean
        get() = canBuyPlan && selectedPlan != null

    val showProceedButton : Boolean
        get() = canBuyPlan
}

enum class ProceedButtonState {
    ENABLED,
    DISABLED,
    Hidden,
    Loading
}

enum class CurrentPlanState {
    ACTIVE,
    EXPIRED,
    NO_ACTIVE_PLAN
}
