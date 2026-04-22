package com.yuma.oemsdk.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuma.oemsdk.data.dto.SdkBatteryDetailsDto
import com.yuma.oemsdk.data.dto.SdkSwapHistoryRequestDto
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

private const val TAG = "SdkProfileViewModel"

// ── State ─────────────────────────────────────────────────────────────────────

data class SdkProfileState(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val phone: String = "",
    val bikeProvider: String = "",
    val bikeNumber: String = "",
    val bikeQrCode: String = "",
    val profileImageUrl: String = "https://yuma-static.s3.ap-south-1.amazonaws.com/oem/image.png",
    val batteryDetails: List<SdkBatteryDetailsDto> = emptyList(),
    val swapHistory: List<SdkSwapDayUiModel> = emptyList(),
    val isLoadingMoreSwaps: Boolean = false,
    val allSwapsLoaded: Boolean = false,
    val error: String? = null
)

data class SdkSwapDayUiModel(val date: String, val swaps: List<SdkSwapItemUiModel>)
data class SdkSwapItemUiModel(val swapTime: String, val serviceTime: String)

sealed class SdkProfileEvent {
    data object LoggedOut : SdkProfileEvent()
    data class Error(val message: String) : SdkProfileEvent()
}

// ── ViewModel ────────────────────────────────────────────────────────────────

internal class SdkProfileViewModel(
    private val prefManager: SdkPrefManager,
    private val remoteDataSource: SdkHomeRemoteDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(SdkProfileState())
    val state: StateFlow<SdkProfileState> = _state.asStateFlow()

    private val _events = MutableStateFlow<SdkProfileEvent?>(null)
    val events: StateFlow<SdkProfileEvent?> = _events.asStateFlow()

    private var currentPage = 1
    private var clientVehicleId: Int = 0

    init { loadUserData() }

    fun onTabVisible() {
        viewModelScope.launch {
            loadUserData()
            loadSwapHistory()
            loadBatteryDetails()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val userJson = prefManager.getUserData() ?: return@launch
            try {
                val obj = Json.parseToJsonElement(userJson).jsonObject
                val firstName = obj["firstName"]?.jsonPrimitive?.content.orEmpty()
                val surname   = obj["surname"]?.jsonPrimitive?.content.orEmpty()
                val phone     = obj["phone"]?.jsonPrimitive?.content.orEmpty()
                clientVehicleId = obj["clientVehicleId"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0

                _state.value = _state.value.copy(
                    fullName     = "$firstName $surname".trim(),
                    phone        = "+91-$phone",
                    bikeProvider = obj["bikeProvider"]?.jsonPrimitive?.content.orEmpty(),
                    bikeNumber   = obj["bikeNumber"]?.jsonPrimitive?.content.orEmpty(),
                    bikeQrCode   = obj["clientVehicleQrCode"]?.jsonPrimitive?.content.orEmpty()
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse user data: ${e.message}")
            }
        }
    }

    fun loadSwapHistory() {
        if (_state.value.allSwapsLoaded || _state.value.isLoadingMoreSwaps) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMoreSwaps = true)
            val result = remoteDataSource.getSwapHistory(
                SdkSwapHistoryRequestDto(clientVehicleId, currentPage, 10)
            )
            when (result) {
                is SdkResult.Success -> {
                    val days = result.data.data?.map { day ->
                        SdkSwapDayUiModel(
                            date = day.date,
                            swaps = day.swaps.map { SdkSwapItemUiModel(it.swapTime, it.serviceTime) }
                        )
                    } ?: emptyList()
                    _state.value = _state.value.copy(
                        swapHistory        = _state.value.swapHistory + days,
                        isLoadingMoreSwaps = false,
                        allSwapsLoaded     = days.isEmpty()
                    )
                    if (days.isNotEmpty()) currentPage++
                }
                is SdkResult.Error -> {
                    _state.value = _state.value.copy(isLoadingMoreSwaps = false, error = result.message)
                }
            }
        }
    }

    private fun loadBatteryDetails() {
        viewModelScope.launch {
            val result = remoteDataSource.getBatteryDetails(clientVehicleId)
            if (result is SdkResult.Success) {
                _state.value = _state.value.copy(batteryDetails = result.data)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val refreshToken = prefManager.getRefreshToken().orEmpty()
            remoteDataSource.logoutUser(
                com.yuma.oemsdk.data.dto.SdkLogoutRequestDto(refreshToken)
            )
            prefManager.logoutUser()
            _events.value = SdkProfileEvent.LoggedOut
        }
    }

    fun consumeEvent() { _events.value = null }

    // ── Factory ───────────────────────────────────────────────────────────────
    internal class Factory(
        private val prefManager: SdkPrefManager,
        private val remoteDataSource: SdkHomeRemoteDataSource
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SdkProfileViewModel(prefManager, remoteDataSource) as T
    }
}
