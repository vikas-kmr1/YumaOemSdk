package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.yumacustomer.core_analytics.api.AnalyticsApi
//import com.yumacustomer.core_logger.api.LoggerApi
import com.yumaoem.core.utils.app_utils.isAndroid12OrLower
import com.yumaoem.core.utils.bluetooth.BluetoothController
import com.yumaoem.core.utils.bluetooth.isBluetoothEnabled
import com.yumaoem.core.utils.bluetooth.model.BluetoothDeviceDomain
import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core.utils.currentTimeMillis
import com.yumaoem.core.utils.global_events.EnableBluetoothEvent
import com.yumaoem.core.utils.global_events.controller.EventController
import com.yumaoem.core.utils.map_style.calculateDistanceInMeters
import com.yumaoem.core.utils.orFalse
import com.yumaoem.core.utils.orZero
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.common.toDomain
import com.yumaoem.feature_home.common.util.analytics_utils.CommonAnalyticsParamsProvider
import com.yumaoem.feature_home.common.util.openMapsDirections
import com.yumaoem.feature_home.data.dto.beacon_details.request.BeaconDetailsRequest
import com.yumaoem.feature_home.domain.usecase.beacon_details.GetBeaconDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.checkin_screen.CancelTokenBookingUseCase
import com.yumaoem.feature_home.domain.usecase.checkin_screen.CheckInUserUseCase
import com.yumaoem.feature_home.domain.usecase.checkin_screen.ObserveTokenExpiryCountdownUseCase
import com.yumaoem.feature_home.domain.usecase.checkin_screen.ValidateLocationUseCase
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationRequestDTO
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import com.yumaoem.feature_home.domain.usecase.get_battery_details.GetBatteryDetailsUseCase
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.state.DialogState
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.state.TokenDetailsScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TokenDetailsViewModel(
    private val bluetoothController: BluetoothController,
    private val observeTokenExpiryCountdownUseCase: ObserveTokenExpiryCountdownUseCase,
    private val getBeaconDetailsUseCase: GetBeaconDetailsUseCase,
    private val checkInUserUseCase: CheckInUserUseCase,
    private val cancelTokenBookingUseCase: CancelTokenBookingUseCase,
    private val prefUtilApi: YumaPrefUtilApi,
    private val commonAnalyticsParamsProvider: CommonAnalyticsParamsProvider,
    private val getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
    private val locationProvider: CoreLocationProvider,
    //private val loggerApi: LoggerApi,
    private val validateLocationUseCase: ValidateLocationUseCase,
   // private val analyticsApi: AnalyticsApi
) : ViewModel() {
    var state by mutableStateOf(TokenDetailsScreenState())
        private set

    private var beaconSearchRetriesLeft = MAX_ALLOWED_BEACON_SEARCH_RETRIES

    private val _uiEvent = MutableSharedFlow<TokenDetailsScreenUiEvent>()
    val uiEvent: SharedFlow<TokenDetailsScreenUiEvent> = _uiEvent

    private val _devices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    val devices: StateFlow<List<BluetoothDeviceDomain>> = _devices

    private var isBeaconMatchFound = false

    private var countdownJob: Job? = null

    private var beaconScanJob: Job? = null

    fun onEvent(event: TokenDetailsScreenEvent) {
        when (event) {
            TokenDetailsScreenEvent.BookingExpiryTryAgainClicked -> {
                viewModelScope.launch {
                    prefUtilApi.removeBookedTokenDetails()
                    state = state.copy(
                        dialogState = DialogState.None
                    )
                }
            }

            TokenDetailsScreenEvent.CancelBookingClicked -> {
                viewModelScope.launch {
                    prefUtilApi.removeBookedTokenDetails()
                    state = state.copy(
                        dialogState = DialogState.CancelBookingConfirmation
                    )
                }
            }

            TokenDetailsScreenEvent.CancelBookingConfirmed -> {
                state = state.copy(
                    dialogState = DialogState.None
                )
                viewModelScope.launch {
                    cancelTokenBookingUseCase.invoke(
                        tokenId = state.bookedTokenDetails?.tokenID?.toInt().orZero()
                    ).collect(
                        onLoading = {
                            state = state.copy(
                                isCancelBookingButtonLoading = true
                            )
                        },
                        onSuccess = {
                            state = state.copy(
                                isCancelBookingButtonLoading = false
                            )
                            sendCancelBookingEvent()
                            prefUtilApi.removeBookedTokenDetails()
                            _uiEvent.emit(TokenDetailsScreenUiEvent.OnBookingCancelled)
                        },
                        onError = { errorMessage, _ ->
                            state = state.copy(
                                isCancelBookingButtonLoading = false
                            )
                            _uiEvent.emit(TokenDetailsScreenUiEvent.ShowSnackbar(errorMessage))
                        }
                    )
                }
            }


            TokenDetailsScreenEvent.CheckInAtStationClicked -> {
                if(isBluetoothEnabled()){
                    handleCheckInAtStation()
                } else {
                    viewModelScope.launch {
                       EventController.sendEvent(EnableBluetoothEvent(
                           onBluetoothEnabled = {
                               handleCheckInAtStation()
                           }
                       ))
                    }
                }
            }

            TokenDetailsScreenEvent.DismissDialog -> {
                state = state.copy(
                    dialogState = DialogState.None
                )
            }

            TokenDetailsScreenEvent.GetDirectionsClicked -> {
                state.bookedTokenDetails?.bookingStation?.location?.latitude?.let {
                    openMapsDirections(
                        latitude = it,
                        longitude = state.bookedTokenDetails?.bookingStation?.location?.longitude!!
                    )
                }
            }

            TokenDetailsScreenEvent.NoBeaconFoundRetry -> {
                state = state.copy(
                    dialogState = DialogState.None
                )
                startBluetoothDevicesScan()
            }
        }
    }

    private fun handleCheckInAtStation() {
        beaconSearchRetriesLeft = MAX_ALLOWED_BEACON_SEARCH_RETRIES
        /**
         * Check if the beacon Details are not null
         */
        if (isAndroid12OrLower()) {
            checkUserDistance()
        } else {
            if (state.beaconDetails != null) {
                startBluetoothDevicesScan()
            } else {
                checkUserDistance()
            }
        }
    }

    private fun getBeaconDetails() {
        viewModelScope.launch {
            val currentLocation = locationProvider.getCurrentLocation()
            getBeaconDetailsUseCase(
                BeaconDetailsRequest(
                    latitude = currentLocation?.latitude.orZero(),
                    longitude = currentLocation?.longitude.orZero(),
                    tokenId = state.bookedTokenDetails?.tokenID?.toInt().orZero()
                )
            ).collect(
                onError = { errorMessage, _ ->
                    state = state.copy(
                        isCheckInButtonLoading = false
                    )
                    //_uiEvent.emit(TokenDetailsScreenUiEvent.ShowSnackbar(errorMessage))
                },
                onSuccess = {
                    state = state.copy(
                        beaconDetails = it,
                        isCheckInButtonLoading = false
                    )
                },
                onLoading = {
                    state = state.copy(
                        isCheckInButtonLoading = true
                    )
                }
            )
        }
    }

    private fun startBluetoothDevicesScan() {
        isBeaconMatchFound = false
        beaconScanJob?.cancel()
        state = state.copy(
            isCheckInButtonLoading = true
        )
        bluetoothController.startDiscovery()
        handleBeaconSearchTimeout()
    }

    private fun handleBeaconSearchTimeout() {
        beaconScanJob = viewModelScope.launch {
            delay(BEACON_SEARCH_TIMEOUT_IN_MILLIS)
            stopBluetoothDevicesScan()
            beaconSearchRetriesLeft -= 1
            if (beaconSearchRetriesLeft == 0) {
                checkUserDistance()
            } else {
                state = state.copy(
                    dialogState = DialogState.NoBeaconFound,
                    isCheckInButtonLoading = false
                )
            }
        }
    }

    private fun checkUserDistance() {
        viewModelScope.launch {
            val userLocation = locationProvider.getCurrentLocation()
            if (userLocation==null){
                SnackbarController.sendEvent(
                    event = SnackbarEvent("User's location not found")
                )
                return@launch
            }

            validateLocation(
                latitude = userLocation.latitude.orZero(),
                longitude = userLocation.longitude.orZero(),
                tokenId = state.bookedTokenDetails?.tokenID?.toInt().orZero()
            )
//            val userDistanceFromStation = calculateDistanceInMeters(
//                startLat = userLocation.latitude.orZero(),
//                startLng = userLocation.longitude.orZero(),
//                endLat = state.bookedTokenDetails?.bookingStation?.location?.latitude!!,
//                endLng = state.bookedTokenDetails?.bookingStation?.location?.longitude!!
//            )
//            if (userDistanceFromStation <= MAXIMUM_ALLOWED_CHECK_IN_DISTANCE_IN_METRES) {
//                checkInUser()
//            } else {
//                state = state.copy(
//                    dialogState = DialogState.ReachStation,
//                    isCheckInButtonLoading = false
//                )
//            }
        }
    }

    private fun stopBluetoothDevicesScan() {
        bluetoothController.stopDiscovery()
    }

    private fun startTokenCountdown(expiryTimestamp: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            observeTokenExpiryCountdownUseCase(expiryTimestamp).collect {
                if (it == "00:00") {
                    state = state.copy(
                        dialogState = DialogState.TokenExpired
                    )
                }
                state = state.copy(
                    expiryTime = it
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            val tokenData = prefUtilApi.getBookedTokenDetails()
            if (tokenData != null) {
                state = state.copy(
                    bookedTokenDetails = tokenData.toDomain(),
                    idDiySwap = tokenData.isDiyToken
                )
                getBeaconDetails()
                startTokenCountdown(expiryTimestamp = tokenData.tokenExpiryTimeStamp)
            }
        }

        viewModelScope.launch {
            bluetoothController.scannedDevices.collect { scannedList ->
                _devices.value = scannedList
                val beaconMacIds: Set<String> =
                    state.beaconDetails?.map { it.macId }?.toSet() ?: emptySet()
                val beaconUuids: Set<String> =
                    state.beaconDetails?.map { it.uuid }?.toSet() ?: emptySet()

                val hasMatch = scannedList.any { it.address in beaconMacIds }
                if (hasMatch && isBeaconMatchFound.not()) {
                    isBeaconMatchFound = true
                    bluetoothController.stopDiscovery()
                    beaconScanJob?.cancel()
                    checkInUser()
                }
            }
        }
    }

    private fun checkInUser() {
        viewModelScope.launch {
            checkInUserUseCase.invoke(
                tokenId = state.bookedTokenDetails?.tokenID?.toInt().orZero()
            ).collect(
                onLoading = {
                    state = state.copy(
                        isCheckInButtonLoading = true
                    )
                },
                onSuccess = {
                    sendCheckInEvent(true)
                    state = state.copy(
                        isCheckInButtonLoading = false
                    )
                    if (state.bookedTokenDetails?.isDiyToken.orFalse()){
                        _uiEvent.emit(TokenDetailsScreenUiEvent.DiySwapStarted)
                    }else{
                        _uiEvent.emit(TokenDetailsScreenUiEvent.CheckedInAtStation)
                    }
                },
                onError = { errorMessage, _ ->
                    state = state.copy(
                        isCheckInButtonLoading = false
                    )
                    _uiEvent.emit(TokenDetailsScreenUiEvent.ShowSnackbar(errorMessage))
                }
            )
        }
    }

    fun validateLocation(latitude: Double, longitude: Double, tokenId: Int) {
        viewModelScope.launch {
            validateLocationUseCase(
                LocationValidationRequestDTO(latitude = latitude, longitude = longitude, tokenId = tokenId)
            ).collect(
                onLoading = {
                    state = state.copy(
                        isCheckInButtonLoading = true
                    )
                },
                onSuccess = {
                    if (it.isLocationValid){
                        checkInUser()
                    }else{
                        state = state.copy(
                            dialogState = DialogState.ReachStation,
                            isCheckInButtonLoading = false
                        )
                        sendCheckInEvent(false)
                    }

                },
                onError = { errorMessage, _ ->
                    sendCheckInEvent(false)
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(errorMessage)
                    )
                }
            )
        }
    }

    override fun onCleared() {
        stopBluetoothDevicesScan()
        super.onCleared()
    }

    fun sendTokenScreenViewed() {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
/*            analyticsApi.postEvent(
                event = "screen_viewed",
                values = commonValues + mapOf(
                    "screen_name" to "Token_Details_Viewed"
                )
            )*/
        }
    }

    private fun sendCheckInEvent(
        isSuccess: Boolean
    ) {
        viewModelScope.launch {
            val status = if (isSuccess) "success" else "failed"
            val currentUser = prefUtilApi.getUserData()
            val tokenData = prefUtilApi.getBookedTokenDetails()
            val location = locationProvider.getCurrentLocation()
     /*       analyticsApi.postEvent(
                event = "check_in_at_station",
                values = mapOf(
                    "user_id" to currentUser?.userId.orEmpty(),
                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
                    "mobile_number" to currentUser?.phone.orEmpty(),
                    "timestamp" to currentTimeMillis(),
                    "latitude" to location?.latitude.orZero(),
                    "longitude" to location?.longitude.orZero(),
                    "station_id" to tokenData?.bookingStation?.stationId.orZero(),
                    "station_name" to tokenData?.bookingStation?.stationName.orEmpty(),
                    "token_id" to tokenData?.tokenID.orEmpty(),
                    "token_number" to tokenData?.tokenNumber.orEmpty(),
                    "status" to status,
                    "bike_qr_number" to tokenData?.clientVehicleQrCode.orEmpty(),
                    "fleet_name" to currentUser?.bikeProvider.toString(),
                    "is_diy" to tokenData?.isDiyToken.toString(),
                )
            )*/
        }
    }

    private fun sendCancelBookingEvent(){
        viewModelScope.launch {
            val currentUser = prefUtilApi.getUserData()
            val tokenData = prefUtilApi.getBookedTokenDetails()
            val location = locationProvider.getCurrentLocation()
/*            analyticsApi.postEvent(
                event = "booking_cancelled",
                values = mapOf(
                    "user_id" to currentUser?.userId.orEmpty(),
                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
                    "mobile_number" to currentUser?.phone.orEmpty(),
                    "timestamp" to currentTimeMillis(),
                    "latitude" to location?.latitude.orZero(),
                    "longitude" to location?.longitude.orZero(),
                    "station_id" to tokenData?.bookingStation?.stationId.orZero(),
                    "station_name" to tokenData?.bookingStation?.stationName.orEmpty(),
                    "token_id" to tokenData?.tokenID.orEmpty(),
                    "token_number" to tokenData?.tokenNumber.orEmpty(),
                    "before_check_in" to true,
                    "token_expired" to false,
                    "bike_qr_number" to tokenData?.clientVehicleQrCode.orEmpty(),
                    "fleet_name" to currentUser?.bikeProvider.toString(),
                    "is_diy" to tokenData?.isDiyToken.toString(),
                )
            )*/
        }
    }
    companion object {
        const val MAXIMUM_ALLOWED_CHECK_IN_DISTANCE_IN_METRES = 100
        const val MAX_ALLOWED_BEACON_SEARCH_RETRIES = 2
        const val BEACON_SEARCH_TIMEOUT_IN_MILLIS: Long = 3000
    }


}


sealed class TokenDetailsScreenUiEvent {
    data object DiySwapStarted : TokenDetailsScreenUiEvent()
    data object CheckedInAtStation : TokenDetailsScreenUiEvent()
    data class ShowSnackbar(val message: String) : TokenDetailsScreenUiEvent()
    data object OnBookingCancelled : TokenDetailsScreenUiEvent()
}
