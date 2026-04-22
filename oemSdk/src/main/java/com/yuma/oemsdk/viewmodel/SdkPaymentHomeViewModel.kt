package com.yuma.oemsdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuma.oemsdk.data.dto.SdkPaymentPlansResponseDto
import com.yuma.oemsdk.data.dto.SdkPlanDto
import com.yuma.oemsdk.data.network.SdkHomeRemoteDataSource
import com.yuma.oemsdk.data.network.SdkResult
import com.yuma.oemsdk.prefs.SdkPrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal data class SdkPaymentHomeState(
    val plans: List<SdkPlanDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPlan: SdkPlanDto? = null
)

internal class SdkPaymentHomeViewModel(
    private val prefManager: SdkPrefManager,
    private val remoteDataSource: SdkHomeRemoteDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(SdkPaymentHomeState())
    val state: StateFlow<SdkPaymentHomeState> = _state.asStateFlow()

    init {
        loadPaymentPlans()
    }

    fun loadPaymentPlans() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val userJson = prefManager.getUserData()
            
            var clientVehicleId = "0"
            if (userJson != null) {
                try {
                    val obj = Json.parseToJsonElement(userJson).jsonObject
                    clientVehicleId = obj["clientVehicleId"]?.jsonPrimitive?.content ?: "0"
                } catch (e: Exception) {
                    android.util.Log.e("SdkPaymentVM", "Error parsing user data: ${e.message}")
                }
            }

            val result = remoteDataSource.getPaymentPlans(clientVehicleId)
            when (result) {
                is SdkResult.Success -> {
                    _state.value = _state.value.copy(
                        plans = result.data.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is SdkResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun selectPlan(plan: SdkPlanDto) {
        _state.value = _state.value.copy(selectedPlan = plan)
    }

    internal class Factory(
        private val prefManager: SdkPrefManager,
        private val remoteDataSource: SdkHomeRemoteDataSource
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SdkPaymentHomeViewModel(prefManager, remoteDataSource) as T
    }
}
