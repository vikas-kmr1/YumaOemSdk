package com.yuma.oemsdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuma.oemsdk.data.network.SdkHomeRemoteDataSource
import com.yuma.oemsdk.location.SdkLocationManager
import com.yuma.oemsdk.prefs.SdkPrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal enum class SdkHomeDestination {
    MapScreen,
    BookedTokenDetailsScreen,
    TokenQrDetailsScreen,
    SwapSuccessScreen,
    ScanMachineQrScreen,
    DiySwapInProgressScreen,
    TagBatteryScannerScreen
}

internal data class SdkHomeState(
    val currentDestination: SdkHomeDestination = SdkHomeDestination.MapScreen,
    val isLoading: Boolean = false,
    val error: String? = null
)

internal class SdkHomeViewModel(
    private val prefManager: SdkPrefManager,
    private val remoteDataSource: SdkHomeRemoteDataSource,
    private val locationManager: SdkLocationManager
) : ViewModel() {

    private val _state = MutableStateFlow(SdkHomeState())
    val state: StateFlow<SdkHomeState> = _state.asStateFlow()

    fun updateDestination(destination: SdkHomeDestination) {
        _state.value = _state.value.copy(currentDestination = destination)
    }

    // TODO: Add logic for booking, check-in, etc. mirroring HomeViewModel.kt

    class Factory(
        private val prefManager: SdkPrefManager,
        private val remoteDataSource: SdkHomeRemoteDataSource,
        private val locationManager: SdkLocationManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SdkHomeViewModel(prefManager, remoteDataSource, locationManager) as T
    }
}
