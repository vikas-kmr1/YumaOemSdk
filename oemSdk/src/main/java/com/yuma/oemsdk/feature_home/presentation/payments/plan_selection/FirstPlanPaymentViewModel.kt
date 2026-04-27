package com.yumaoem.feature_home.presentation.payments.plan_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumaoem.feature_home.presentation.payments.plan_selection.event.FirstPlanPaymentEvent
import com.yumaoem.feature_home.presentation.payments.plan_selection.state.FirstPaymentScreenState
import com.yumaoem.feature_home.presentation.payments.plan_selection.state.PaymentPlanGroup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FirstPlanPaymentViewModel : ViewModel() {

    private val _state = MutableStateFlow(FirstPaymentScreenState())
    val state: StateFlow<FirstPaymentScreenState> = _state

    init {
        viewModelScope.launch {
            val planGroups = generateDummyPaymentPlans()
            _state.value = _state.value.copy(
                planGroups = planGroups,
                selectedPlanGroupId = planGroups.firstOrNull()?.planTypeId
            )
        }
    }

    fun onEvent(event: FirstPlanPaymentEvent) {
        when (event) {
            is FirstPlanPaymentEvent.PlanSelected -> handlePlanSelected(event.plan)
            is FirstPlanPaymentEvent.OnTabChanged -> handleTabChanged(event.planTypeId)
            FirstPlanPaymentEvent.ProceedClicked -> handleProceedClicked()
        }
    }

    private fun handlePlanSelected(plan: PaymentPlanItem) {
        val updatedGroups = _state.value.planGroups.map { group ->
            group.copy(
                planItems = group.planItems.map { item ->
                    item.copy(isSelected = item.id == plan.id)
                }
            )
        }

        _state.value = _state.value.copy(
            planGroups = updatedGroups,
            selectedPlan = plan,
            isProceedButtonEnabled = true
        )
    }

    private fun handleTabChanged(planTypeId: Int) {
        _state.value = _state.value.copy(
            selectedPlanGroupId = planTypeId,
            selectedPlan = null,
            isProceedButtonEnabled = false
        )
    }

    private fun handleProceedClicked() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            delay(500)
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}


fun generateDummyPaymentPlans(): List<PaymentPlanGroup> {
    return listOf(
        PaymentPlanGroup(
            planTypeId = 0,
            planType = "7 Days",
            planItems = listOf(
                PaymentPlanItem(
                    id = 1,
                    title = "Basic 7D",
                    range = "0–50 kWh",
                    validity = "7 days",
                    price = "$5",
                    isSelected = false
                ),
                PaymentPlanItem(
                    id = 2,
                    title = "Standard 7D",
                    range = "51–150 kWh",
                    validity = "7 days",
                    price = "$8",
                    isSelected = false
                )
            )
        ),
        PaymentPlanGroup(
            planTypeId = 1,
            planType = "15 Days",
            planItems = listOf(
                PaymentPlanItem(
                    id = 3,
                    title = "Basic 15D",
                    range = "0–100 kWh",
                    validity = "15 days",
                    price = "$10",
                    isSelected = false
                ),
                PaymentPlanItem(
                    id = 4,
                    title = "Standard 15D",
                    range = "101–250 kWh",
                    validity = "15 days",
                    price = "$15",
                    isSelected = false
                )
            )
        ),
        PaymentPlanGroup(
            planTypeId = 2,
            planType = "30 Days",
            planItems = listOf(
                PaymentPlanItem(
                    id = 5,
                    title = "Premium 30D",
                    range = "151–300 kWh",
                    validity = "30 days",
                    price = "$20",
                    isSelected = false
                ),
                PaymentPlanItem(
                    id = 6,
                    title = "Unlimited 30D",
                    range = "301+ kWh",
                    validity = "30 days",
                    price = "$35",
                    isSelected = false
                )
            )
        )
    )
}
