package com.yumaoem.feature_home.presentation.home_screen.tag_battery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core.utils.currentTimeMillis
import com.yumaoem.core.utils.qr_validator.BatteryQrValidator
import com.yumaoem.core.utils.qr_validator.QR_Patterns.BATTERY_CODE_SEPARATOR
import com.yumaoem.core.utils.vibration.vibrate
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.common.util.analytics_utils.CommonAnalyticsParamsProvider
import com.yumaoem.feature_home.common.customer_support.CustomerSupportCallInteractor
import com.yumaoem.feature_home.data.dto.tag_battery.request.TagBatteryRequestDTO
import com.yumaoem.feature_home.domain.usecase.tag_battery.MapNewBatteriesOnBikeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TagBatteryViewModel(
    private val mapNewBatteriesOnBikeUseCase: MapNewBatteriesOnBikeUseCase,
    private val prefsApi: YumaPrefUtilApi,
    private val commonAnalyticsParamsProvider: CommonAnalyticsParamsProvider,
    //private val analyticsApi: AnalyticsApi,
    private val coreLocationProvider: CoreLocationProvider,
    private val customerSupportCallInteractor: CustomerSupportCallInteractor
): ViewModel() {

    private val _state = MutableStateFlow(TagBatteryScreenState())
    val state: StateFlow<TagBatteryScreenState> = _state

    private val _uiEvent = Channel<TagBatteryUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var submitBatteryJob: Job? = null
    private var launchedTimeStamp = 0L

    init {
        viewModelScope.launch {
            val userDetails = prefsApi.getUserData()
            if (userDetails != null) {
                _state.value = _state.value.copy(
                    totalBatteryCount = userDetails.batteryCount
                )
            }
        }
    }

    fun onEvent(event: DiyScanBatteryIntent) {
        when (event) {
            DiyScanBatteryIntent.OnFlashLightClicked -> {
              _state.update { current ->
                  current.copy(
                      isFlashLightOn = !current.isFlashLightOn
                  )
              }
            }
            is DiyScanBatteryIntent.OnScanCompleted -> {
                onBatteryScanned(event)
            }
            DiyScanBatteryIntent.RetryScan -> {}

            DiyScanBatteryIntent.OnScreenViewed -> {
                launchedTimeStamp = currentTimeMillis()
                sendTagBatteryScreenViewedEvent(false)
            }
        }
    }

    private fun onBatteryScanned(event: DiyScanBatteryIntent.OnScanCompleted) {
        if (_state.value.isSubmitting.not()) {
            validateAndStoreBatteryQr(event.scannedCode,event.isManualEntry)
            }
    }

    private fun validateAndStoreBatteryQr(batteryQr: String, isManualEntry: Boolean) {
        if (batteryQr.isEmpty() || _state.value.isSubmitting) return

        val isValid = BatteryQrValidator.validateBatteryQr(input = batteryQr)
        val alreadyExists = state.value.batteryQrList.contains(batteryQr)

        when {
            !isValid -> {
                sendTagBatteryScannedEvent(false, "Invalid QR Code", isManualEntry)
                sendError("Invalid QR Code")
            }

            alreadyExists -> {}

            else -> {
                saveBatteryQr(batteryQr)
            }
        }
    }

    @androidx.annotation.RequiresPermission(android.Manifest.permission.VIBRATE)
    private fun saveBatteryQr(batteryQr: String) {
        val newQr = batteryQr.substringBefore(BATTERY_CODE_SEPARATOR)

        _state.update { state ->
            if (newQr in state.batteryQrList) return@update state

            vibrate(durationMillis = 150)

            val updatedList = state.batteryQrList + newQr

            if (updatedList.size == state.totalBatteryCount) {
                submitChargedBatteryQr(updatedList)
            }

            state.copy(batteryQrList = updatedList)
        }
    }

    private fun submitChargedBatteryQr(updatedList: List<String>) {
        submitBatteryJob?.cancel()

        submitBatteryJob = viewModelScope.launch {
            delay(5_000)

            val userDetails = prefsApi.getUserData()
            if (userDetails == null) {
                sendError("User details not found. Please login again.")
                return@launch
            }

            val currentLocation = coreLocationProvider.getCurrentLocation()
            if (currentLocation == null) {
                sendError("Unable to fetch location. Please enable GPS and try again.")
                return@launch
            }

            val clientBikeQr = userDetails.clientVehicleQrCode

            mapNewBatteriesOnBikeUseCase(
                request = TagBatteryRequestDTO(
                    vehicleQrCode = clientBikeQr,
                    clientVehicleId = userDetails.clientVehicleId,
                    currentBatteryQrcodes = updatedList,
                    userId = userDetails.userId.toInt(),
                    clientUserId = userDetails.clientUserId,
                    batteryCount = userDetails.batteryCount,
                    currentLatitude = currentLocation.latitude,
                    currentLongitude = currentLocation.longitude
                )
            ).collect(
                onLoading = {
                    _state.value = _state.value.copy(isSubmitting = true)
                },
                onSuccess = {
                    sendTagBatteryScreenViewedEvent(true)
                    _uiEvent.send(TagBatteryUiEvent.NavigateToMapScreen)
                },
                onError = { message, _ ->
                    sendError(message)
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        batteryQrList = emptyList()
                    )
                }
            )
        }
    }

    fun sendError(message: String) {
        viewModelScope.launch {
            _uiEvent.send(TagBatteryUiEvent.ShowError(message))
        }
    }
    fun showCustomerSupportBottomSheet() {
        viewModelScope.launch {
            val mobileNumber = prefsApi.getUserData()?.phone
            _state.value = _state.value.copy(
                bottomSheet = TagBatteryBottomSheet.GetCallbackBottomSheet(
                    mobileNumber = mobileNumber.orEmpty(),
                    isLoading = false
                )
            )
        }
    }

    fun dismissBottomSheet() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                bottomSheet = TagBatteryBottomSheet.None
            )
        }
    }


    fun requestCall (contactNumber: String) {
        viewModelScope.launch {
            customerSupportCallInteractor
                .requestCall(contactNumber)
                .collect(
                    onLoading = {
                        _state.value = _state.value.copy(
                            bottomSheet = TagBatteryBottomSheet.GetCallbackBottomSheet(
                                mobileNumber = contactNumber,
                                isLoading = true
                            )
                        )
                    },
                    onSuccess = {
                        _state.value = _state.value.copy(
                            bottomSheet = TagBatteryBottomSheet.None
                        )

                        _uiEvent.send(TagBatteryUiEvent.ShowSuccessSnackbar(it.message))
                    },
                    onError = { errorMessage, _ ->
                        viewModelScope.launch {
                            _uiEvent.send(TagBatteryUiEvent.ShowError(errorMessage))
                        }
                        _state.value = _state.value.copy(
                            bottomSheet = TagBatteryBottomSheet.None
                        )
                    }
                )
        }
    }


    fun sendTagBatteryScreenViewedEvent(isFinalEvent: Boolean = false) {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()

            val eventValues = mutableMapOf<String, String>(
                "screen_name" to "tag_battery_screen_viewed"
            )

            if (isFinalEvent) {
                eventValues["time_on_page"] =
                    (currentTimeMillis() - launchedTimeStamp).toString()
            }

//            analyticsApi.postEvent(
//                event = "screen_viewed",
//                values = commonValues + eventValues
//            )
        }
    }

    fun sendTagBatteryScannedEvent(isSuccess: Boolean, message: String, isManualEntry: Boolean) {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
//            analyticsApi.postEvent(
//                event = "tag_battery_scanned",
//                values = commonValues + mapOf(
//                   "battery_qr_codes" to state.value.batteryQrList.joinToString(","),
//                    "status" to if (isSuccess) "success" else "failure",
//                    "failure_reason" to message,
//                    "is_QR_scan" to !isManualEntry,
//                )
            //)
        }
    }
}


