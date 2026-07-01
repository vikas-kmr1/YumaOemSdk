package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuma.oemsdk.YumaSdk
import com.yumaoem.core.utils.orZero
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.data.dto.whatsapp_support_details.SupportDetailsRequestDto
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import com.yumaoem.feature_home.domain.usecase.checkin_screen.CancelTokenBookingUseCase
import com.yumaoem.feature_home.domain.usecase.get_battery_details.GetBatteryDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.support_details.GetWhatsappSupprtDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.token_status.GetTokenStatusUseCase
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.user_current_location_provider.LocationProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TokenQrScreenViewModel(
    private val prefUtilApi: YumaPrefUtilApi,
    private val cancelTokenBookingUseCase: CancelTokenBookingUseCase,
    private val getTokenStatusUseCase: GetTokenStatusUseCase,
    private val supportDetailsUseCase: GetWhatsappSupprtDetailsUseCase,
    private val yumaPrefUtil: YumaPrefUtilApi,
    private val locationProvider: LocationProvider,
    //private val analyticsApi: AnalyticsApi,
    private val getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
) : ViewModel() {

    private val _state = mutableStateOf(TokenQrScreenState())
    val state: State<TokenQrScreenState> = _state

    private val _uiEvent = MutableSharedFlow<TokenQRScreenUiEvent>()
    val uiEvent: SharedFlow<TokenQRScreenUiEvent> = _uiEvent

    var pollingJob: Job? = null

    init {
        viewModelScope.launch {
            val tokenData = prefUtilApi.getBookedTokenDetails()
            if (tokenData != null) {
                val tokenNumber = tokenData.tokenNumber.toInt()
                val vehicleQrCode = tokenData.clientVehicleQrCode
                val bikeNumber = tokenData.clientVehicleQrCode
                val tokenId = tokenData.tokenID

                _state.value = _state.value.copy(
                    tokenId = tokenId.toInt(),
                    tokenNumber = tokenNumber,
                    tokenQRCodeData = vehicleQrCode,
                    bikeNumber = bikeNumber
                )
                startPollingTokenStatus()
            }

        }
    }

    private fun loadBatteryDetails(
        onBatteryDetailsLoaded: (List<BatteryDetails>) -> Unit
    ) {
        viewModelScope.launch {
            val clientId = prefUtilApi.getUserData()?.clientVehicleId.orZero()
            getBatteryDetailsUseCase.invoke(
                clientVehicleId = clientId
            ).collect(
                onLoading = {},
                onSuccess = {
                    onBatteryDetailsLoaded(it)
                },
                onError = { errorMessage, _ ->
                    println("ProfileViewModel: $errorMessage")
                }
            )
        }
    }

    fun onEvent(event: TokenQrScreenEvent) {
        when (event) {
            is TokenQrScreenEvent.DismissDialog -> {
                _state.value = _state.value.copy(
                    cancelBookingDialogVisible = false
                )
            }

            TokenQrScreenEvent.CancelBookingClicked -> {
                _state.value = _state.value.copy(
                    cancelBookingDialogVisible = true
                )
            }

            TokenQrScreenEvent.CancelBookingConfirmed -> {
                _state.value = _state.value.copy(
                    cancelBookingDialogVisible = false
                )
                cancelTokenBooking()
            }
        }
    }

    private fun startPollingTokenStatus() {
        pollingJob = viewModelScope.launch {
            while (isActive) {
                var shouldContinue = true

                getTokenStatusUseCase.invoke(state.value.tokenId, YumaSdk.getConfig().clientSecret)
                    .collect(
                        onLoading = {},
                        onSuccess = { tokenStatus ->
                            getCustomerSupportData(tokenStatus.tokenStatusId)
                            when (tokenStatus.tokenStatusId) {
                                3 -> {
                                    sendSwapCompleteEvent()
                                    viewModelScope.launch {
                                        prefUtilApi.removeBookedTokenDetails()
                                        _uiEvent.emit(
                                            TokenQRScreenUiEvent.TokenStatusUpdated(
                                                TokenStatus.SWAP_COMPLETED,
                                                tokenStatus.swapTime
                                            )
                                        )
                                    }
                                    shouldContinue = false
                                }

                                2 -> {
                                    _uiEvent.emit(
                                        TokenQRScreenUiEvent.TokenStatusUpdated(
                                            TokenStatus.CHECKED_ID
                                        )
                                    )
                                    _state.value = _state.value.copy(
                                        isSwapInProgress = false
                                    )
                                }

                                6 -> {
                                    sendSwapStartedEvent()
                                    _uiEvent.emit(
                                        TokenQRScreenUiEvent.TokenStatusUpdated(
                                            TokenStatus.SWAP_IN_PROGRESS
                                        )
                                    )
                                    _state.value = _state.value.copy(
                                        isSwapInProgress = true
                                    )
                                }
                            }
                        },
                        onError = { _, _ -> }
                    )

                if (!shouldContinue) break

                delay(2000L)
            }
        }
    }


    private fun cancelTokenBooking() {
        viewModelScope.launch {
            cancelTokenBookingUseCase.invoke(
                tokenId = state.value.tokenId
            ).collect(
                onLoading = {
                    _state.value = _state.value.copy(
                        isCancelBookingButtonLoading = true
                    )
                },
                onError = { errorMessage, _ ->
                    _state.value = _state.value.copy(
                        isCancelBookingButtonLoading = false
                    )
                    sendCancelBookingEvent(isSuccess = false)
                    _uiEvent.emit(TokenQRScreenUiEvent.ShowSnackbar(errorMessage))
                },
                onSuccess = {
                    _state.value = _state.value.copy(
                        isCancelBookingButtonLoading = false
                    )
                    sendCancelBookingEvent(isSuccess = true)
                    pollingJob?.cancel()
                    prefUtilApi.removeBookedTokenDetails()
                    _uiEvent.emit(TokenQRScreenUiEvent.OnBookingCancelled)
                }
            )
        }
    }

    private fun getCustomerSupportData(
        statusId: Int
    ) {
        if (state.value.tokenStatusUpdatedForStatus == statusId) return
        viewModelScope.launch {
            val userDetails = yumaPrefUtil.getUserData()
            val currentLocation = locationProvider.getCurrentLocation()
            delay(1000)
            supportDetailsUseCase.invoke(
                SupportDetailsRequestDto(
                    clientUserId = userDetails?.clientUserId.orZero(),
                    clientVehicleId = userDetails?.clientVehicleId.orZero(),
                    latitude = currentLocation?.latitude.orZero(),
                    longitude = currentLocation?.longitude.orZero()
                )
            ).collect(
                onLoading = {},
                onSuccess = {
                    _state.value = _state.value.copy(
                        tokenStatusUpdatedForStatus = statusId
                    )
                    yumaPrefUtil.saveSupportDetails(it.phoneNumber, it.defaultMessage)
                },
                onError = { _, _ -> }
            )
        }
    }

    private fun sendCancelBookingEvent(isSuccess: Boolean) {
        val status = if (isSuccess) "success" else "failed"
        viewModelScope.launch {
            val currentUser = prefUtilApi.getUserData()
            val tokenData = prefUtilApi.getBookedTokenDetails()
            /*            analyticsApi.postEvent(
                            event = "booking_cancelled",
                            values = mapOf(
                                "user_id" to currentUser?.userId.orEmpty(),
                                "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
                                "mobile_number" to currentUser?.phone.orEmpty(),
                                "timestamp" to currentTimeMillis(),
                                "latitude" to locationProvider.getCurrentLocation()?.latitude.orZero(),
                                "longitude" to locationProvider.getCurrentLocation()?.longitude.orZero(),
                                "station_id" to tokenData?.bookingStation?.stationId.orZero(),
                                "station_name" to tokenData?.bookingStation?.stationName.orEmpty(),
                                "token_id" to tokenData?.tokenID.orEmpty(),
                                "token_number" to tokenData?.tokenNumber.orEmpty(),
                                "before_check_in" to false,
                                "token_expired" to false,
                                "bike_qr_number" to tokenData?.clientVehicleQrCode.orEmpty(),
                                "fleet_name" to currentUser?.bikeProvider.toString(),
                                "is_diy" to tokenData?.isDiyToken.toString(),
                                "status" to status
                            )
                        )*/
        }
    }


    private fun sendSwapStartedEvent() {
        if (state.value.isSwapStartedEventSent) return
        loadBatteryDetails(onBatteryDetailsLoaded = { batteryDetails ->
            viewModelScope.launch {
                _state.value = _state.value.copy(
                    isSwapStartedEventSent = true
                )
                val currentUser = prefUtilApi.getUserData()
                val tokenData = prefUtilApi.getBookedTokenDetails()
                /*                analyticsApi.postEvent(
                                    event = "swap_started",
                                    values = mapOf(
                                        "user_id" to currentUser?.userId.orEmpty(),
                                        "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
                                        "mobile_number" to currentUser?.phone.orEmpty(),
                                        "timestamp" to currentTimeMillis(),
                                        "station_id" to tokenData?.bookingStation?.stationId.orZero(),
                                        "battery_details" to batteryDetails.toString()
                                    )
                                )*/
            }
        })
    }

    private fun sendSwapCompleteEvent() {
        if (state.value.isSwapCompletedEventSent) return
        loadBatteryDetails(onBatteryDetailsLoaded = { batteryDetails ->
            viewModelScope.launch {
                _state.value = _state.value.copy(
                    isSwapCompletedEventSent = true
                )
                val currentUser = prefUtilApi.getUserData()
                val tokenData = prefUtilApi.getBookedTokenDetails()
//                analyticsApi.postEvent(
//                    event = "swap_complete",
//                    values = mapOf(
//                        "user_id" to currentUser?.userId.orEmpty(),
//                        "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
//                        "mobile_number" to currentUser?.phone.orEmpty(),
//                        "timestamp" to currentTimeMillis(),
//                        "station_id" to tokenData?.bookingStation?.stationId.orZero(),
//                        "battery_details" to batteryDetails.toString(),
//                        "is_diy" to tokenData?.isDiyToken.toString()
//                    )
//                )
            }
        })

    }

    class Factory(
        private val prefUtilApi: YumaPrefUtilApi,
        private val cancelTokenBookingUseCase: CancelTokenBookingUseCase,
        private val getTokenStatusUseCase: GetTokenStatusUseCase,
        private val supportDetailsUseCase: GetWhatsappSupprtDetailsUseCase,
        private val yumaPrefUtil: YumaPrefUtilApi,
        private val locationProvider: LocationProvider,
        //private val analyticsApi: AnalyticsApi,
        private val getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TokenQrScreenViewModel(
                prefUtilApi = prefUtilApi,
                cancelTokenBookingUseCase = cancelTokenBookingUseCase,
                getTokenStatusUseCase = getTokenStatusUseCase,
                supportDetailsUseCase = supportDetailsUseCase,
                yumaPrefUtil = yumaPrefUtil,
                locationProvider = locationProvider,
                //analyticsApi = analyticsApi,
                getBatteryDetailsUseCase = getBatteryDetailsUseCase,
            ) as T
    }

}

enum class TokenStatus {
    SWAP_IN_PROGRESS,
    CHECKED_ID,
    SWAP_COMPLETED
}

sealed class TokenQRScreenUiEvent {
    data class ShowSnackbar(val message: String) : TokenQRScreenUiEvent()
    data object OnBookingCancelled : TokenQRScreenUiEvent()
    data class TokenStatusUpdated(val tokenStatus: TokenStatus, val swapTime: String = "") :
        TokenQRScreenUiEvent()
}