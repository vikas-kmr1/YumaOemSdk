package com.yuma.oemsdk.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuma.oemsdk.data.dto.SdkAllStationsRequestDto
import com.yuma.oemsdk.data.dto.SdkBookTokenRequestDto
import com.yuma.oemsdk.data.dto.SdkStationDto
import com.yuma.oemsdk.data.network.SdkHomeRemoteDataSource
import com.yuma.oemsdk.data.network.SdkResult
import com.yuma.oemsdk.location.SdkLocationManager
import com.yuma.oemsdk.prefs.SdkPrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal data class SdkMapState(
    val stations: List<SdkStationDto> = emptyList(),
    val selectedStation: SdkStationDto? = null,
    val isLoading: Boolean = false,
    val isBookingInProgress: Boolean = false,
    val error: String? = null
)

internal class SdkMapViewModel(
    private val prefManager: SdkPrefManager,
    private val remoteDataSource: SdkHomeRemoteDataSource,
    private val locationManager: SdkLocationManager
) : ViewModel() {

    private val _mapState = MutableStateFlow(SdkMapState())
    val mapState: StateFlow<SdkMapState> = _mapState.asStateFlow()

    fun loadStations() {
        viewModelScope.launch {
            Log.d("TAG", "loadStations:called ")
            _mapState.value = _mapState.value.copy(isLoading = true)
//            val userJson = prefManager.getUserData() ?: return@launch
            val location = locationManager.currentLocation.value ?: return@launch

            try {
                //val obj = Json.parseToJsonElement(userJson).jsonObject
                val userId = 7537873 // obj["userId"]?.jsonPrimitive?.content.orEmpty()
                val clientCityId = 8//obj["clientCityId"]?.jsonPrimitive?.content?.toInt() ?: 0
                val clientVehicleId = 837 // obj["clientVehicleId"]?.jsonPrimitive?.content?.toInt() ?: 0

                val result = remoteDataSource.getNearbyStations(
                    SdkAllStationsRequestDto(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        clientCityId = clientCityId,
                        clientVehicleId = clientVehicleId,
                        userId = userId.toString()
                    )
                )
                Log.d("TAG", "loadStations: $result")
                when (result) {
                    is SdkResult.Success -> {
                        _mapState.value = _mapState.value.copy(
                            stations = result.data.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                    is SdkResult.Error -> {
                        _mapState.value = _mapState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("TAG", "loadStations: $e")

                _mapState.value = _mapState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onStationSelected(station: SdkStationDto) {
        _mapState.value = _mapState.value.copy(selectedStation = station)
    }

    fun bookToken(station: SdkStationDto) {
        viewModelScope.launch {
            _mapState.value = _mapState.value.copy(isBookingInProgress = true)
            val userJson = prefManager.getUserData() ?: return@launch
            val obj = Json.parseToJsonElement(userJson).jsonObject
            val userId = obj["userId"]?.jsonPrimitive?.content.orEmpty()
            val clientVehicleId = obj["clientVehicleId"]?.jsonPrimitive?.content?.toInt() ?: 0

            val result = remoteDataSource.bookToken(
                SdkBookTokenRequestDto(
                    csId = station.csId,
                    clientUserId = userId,
                    clientVehicleId = clientVehicleId
                )
            )

            when (result) {
                is SdkResult.Success -> {
                    _mapState.value = _mapState.value.copy(isBookingInProgress = false)
                    // TODO: Notify navigation to move to BookedTokenDetailsScreen
                }
                is SdkResult.Error -> {
                    _mapState.value = _mapState.value.copy(
                        isBookingInProgress = false,
                        error = result.message
                    )
                }
            }
        }
    }

    internal class Factory(
        private val prefManager: SdkPrefManager,
        private val remoteDataSource: SdkHomeRemoteDataSource,
        private val locationManager: SdkLocationManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SdkMapViewModel(prefManager, remoteDataSource, locationManager) as T
    }
}
