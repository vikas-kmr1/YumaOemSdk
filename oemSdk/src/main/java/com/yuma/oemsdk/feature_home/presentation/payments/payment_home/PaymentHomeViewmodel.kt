package com.yumaoem.feature_home.presentation.payments.payment_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumacustomer.core_logger.api.LoggerApi
import com.yumaoem.core.utils.orFalse
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.domain.usecase.payments.payment_home.GetPaymentPlansUseCase
import com.yumaoem.feature_home.presentation.payments.payment_home.state.CurrentPlanState
import com.yumaoem.feature_home.presentation.payments.payment_home.state.PaymentHomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentHomeViewModel(
    private val getPaymentPlansUseCase: GetPaymentPlansUseCase,
    private val preferenceApi: YumaPrefUtilApi,
    private val loggerApi: LoggerApi
) : ViewModel() {

    private val _state: MutableStateFlow<PaymentHomeState?> = MutableStateFlow(null)
    val state: StateFlow<PaymentHomeState?> = _state

    fun onEvent(event: PaymentHomeEvent) {
        when (event) {

            is PaymentHomeEvent.OnTabChanged -> {
                val clearedGroups = _state.value?.planGroups?.map { group ->
                    group.copy(
                        plans = group.plans.map { it.copy(isSelected = false) }
                    )
                }

                _state.update { current ->
                    current?.copy(
                        selectedPlanGroupId = event.planGroupId,
                        selectedPlan = null,
                        planGroups = clearedGroups ?: emptyList()
                    )
                }
            }

            is PaymentHomeEvent.PlanSelected -> {
                if (_state.value?.canBuyPlan.orFalse().not()) return
                val selectedPlan = event.plan

                val updatedGroups = _state.value?.planGroups?.map { group ->
                    if (group.groupName == _state.value?.selectedPlanGroupId) {
                        group.copy(
                            plans = group.plans.map { item ->
                                item.copy(isSelected = item.id == selectedPlan.id)
                            }
                        )
                    } else group
                }

                _state.update {
                    it?.copy(
                        planGroups = updatedGroups!!,
                        selectedPlan = selectedPlan,
                    )
                }
            }

            PaymentHomeEvent.ProceedClicked -> {}
        }
    }

    fun refreshPaymentData(
        shouldShowLoader: Boolean
    ) {
        viewModelScope.launch {
            val user = preferenceApi.getUserData()
            getPaymentData(
                clientVehicleId = user?.clientVehicleId,
                shouldShowLoader = shouldShowLoader
            )
        }
    }

    private suspend fun getPaymentData(clientVehicleId: Int?, shouldShowLoader: Boolean) {
        getPaymentPlansUseCase(clientVehicleId.toString()).collect(
            onLoading = {
                if (shouldShowLoader){
                    _state.update { it?.copy(isLoading = true) }
                }
                loggerApi.logDWithTag("PaymentHomeViewModel", "Loading")
            },
            onError = { errorMessage, errorCode ->
                _state.update { it?.copy(isLoading = false) }
                loggerApi.logDWithTag("PaymentHomeViewModel", "Error ${errorMessage}")
            },
            onSuccess = { data ->
                loggerApi.logDWithTag("PaymentHomeViewModel", "Success $data")

                val currentState = _state.value
                val previouslySelectedGroup = currentState?.selectedPlanGroupId
                val previouslySelectedPlanId = currentState?.selectedPlan?.id

                val updatedPlanGroups = data.allPlans.map { group ->
                    if (group.groupName == previouslySelectedGroup) {
                        group.copy(
                            plans = group.plans.map { plan ->
                                plan.copy(isSelected = plan.id == previouslySelectedPlanId)
                            }
                        )
                    } else {
                        group.copy(plans = group.plans.map { it.copy(isSelected = false) })
                    }
                }

                _state.value = PaymentHomeState(
                    header = data.header,
                    currentPlanDetails = data.currentPlanDetails,
                    plansSectionHeading = data.plansSectionHeading,
                    planGroups = updatedPlanGroups,
                    footerNotes = data.footerNotes,
                    selectedPlanGroupId = previouslySelectedGroup ?: data.allPlans.firstOrNull()?.groupName,
                    selectedPlan = updatedPlanGroups
                        .firstOrNull { it.groupName == previouslySelectedGroup }
                        ?.plans
                        ?.firstOrNull { it.id == previouslySelectedPlanId },
                    canBuyPlan = data.canBuyPlan,
                    isLoading = false
                )
            }

        )
    }
}
