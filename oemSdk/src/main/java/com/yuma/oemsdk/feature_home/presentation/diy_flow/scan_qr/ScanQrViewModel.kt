package com.yumaoem.feature_home.presentation.diy_flow.scan_qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yumacustomer.core_logger.api.LoggerApi
import com.yumaoem.core.model.auth.User
import com.yumaoem.core.utils.global_events.HideKeyboard
import com.yumaoem.core.utils.global_events.controller.EventController
import com.yumaoem.core.utils.orFalse
import com.yumaoem.core.utils.orZero
import com.yumaoem.core.utils.vibration.vibrate
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.corepreference.model.BookedTokenDetailsDTO
import com.yumaoem.feature_home.common.util.analytics_utils.CommonAnalyticsParamsProvider
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerDetail
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerRequest
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapRequestDto
import com.yumaoem.feature_home.domain.usecase.auto_dialer.AutoDialerRequestUseCase
import com.yumaoem.feature_home.domain.usecase.diy_flow.StartDiyFlowUseCase
import com.yumaoem.feature_home.domain.usecase.revert_token_status.RevertTokenCheckInStatusUseCase
import com.yumaoem.feature_home.presentation.diy_flow.scan_qr.QrScannerUiEvent.NavigateToSwapInProgress
import com.yumaoem.feature_home.presentation.diy_flow.scan_qr.QrScannerUiEvent.OnTokenCheckInReverted
import com.yumaoem.feature_home.presentation.diy_flow.scan_qr.QrScannerUiEvent.ShowError
import com.yumaoem.feature_home.presentation.diy_flow.scan_qr.ScanQrUiStateBottomSheet.GetCallbackBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.scan_qr.ScanQrUiStateBottomSheet.IncorrectQRModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.scan_qr.ScanQrUiStateBottomSheet.None
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScanQrViewModel(
    private val startDiyFlowUseCase: StartDiyFlowUseCase,
    private val revertTokenCheckInStatusUseCase: RevertTokenCheckInStatusUseCase,
    private val commonAnalyticsParamsProvider: CommonAnalyticsParamsProvider,
    private val autoDialerRequestUseCase: AutoDialerRequestUseCase,
    private val preferenceApi: YumaPrefUtilApi,
    //private val analyticsApi: AnalyticsApi,
    private val loggerApi: LoggerApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanQrUiState())
    val uiState: StateFlow<ScanQrUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<QrScannerUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: ScanQrEvent) {
        when (event) {
            is ScanQrEvent.ToggleFlashlight -> {
                _uiState.value = _uiState.value.copy(
                    isFlashlightOn = !_uiState.value.isFlashlightOn
                )
            }

            is ScanQrEvent.OnBackClicked -> {
                revertTokenStatus()
            }

            is ScanQrEvent.DismissBottomSheet -> {
                _uiState.value = _uiState.value.copy(
                    bottomSheet = None
                )
            }

            is ScanQrEvent.OnScanCompleted -> {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(isScanning = false)
                    if (_uiState.value.scannedQrCode == event.result) return@launch
                    _uiState.update {
                        it.copy(
                            isQrScan = event.isQrScan
                        )
                    }
                    vibrate(200)
                    _uiState.value = _uiState.value.copy(
                        scannedQrCode = event.result
                    )
                    if (isValidCode(event.result)) {
                        viewModelScope.launch {
                            startDiyFlow(event.result)
                        }
                    } else {
                        viewModelScope.launch {
                            sendScanYcuCompleteEvent(false, "Invalid QR Code")
                            EventController.sendEvent(HideKeyboard)
                            delay(200)
                            _uiState.value = _uiState.value.copy(
                                bottomSheet = IncorrectQRModalBottomSheet
                            )
                        }

                    }
                }
            }

            ScanQrEvent.Disposed -> {
                viewModelScope.launch {
                    _uiState.value = uiState.value.copy(
                        scannedQrCode = null,
                        isQrScan = true
                    )
                }
            }

            ScanQrEvent.OnCustomerSupportClicked -> {
                viewModelScope.launch {
                    val contactNumber = preferenceApi.getUserData()?.phone
                    _uiState.value = _uiState.value.copy(
                        bottomSheet = GetCallbackBottomSheet(
                            mobileNumber = contactNumber.orEmpty(),
                            isLoading = false
                        )
                    )
                }
            }

            is ScanQrEvent.OnAutoDialerRequestReceived -> {
                requestCall(event.contactNumber)
            }
        }
    }

    private fun requestCall(contactNumber: String) {
        viewModelScope.launch {
            val userDetails: User? = preferenceApi.getUserData()
            val tokenDetails: BookedTokenDetailsDTO? = preferenceApi.getBookedTokenDetails()

            if (userDetails == null) {
                return@launch
            }

            val fullName = "${userDetails.firstName} ${userDetails.surname}".trim()

            val autoDialerRequestDTO = AutoDialerRequest(
                autoDialerDetails = listOf(
                    AutoDialerDetail(
                        clientId = userDetails.clientId,
                        clientUserId = userDetails.clientUserId,
                        clientCityId = userDetails.clientCityId,
                        userName = fullName,
                        userPhoneNumber = contactNumber,
                        bikeNumber = userDetails.clientVehicleQrCode,
                        yumaClientTokenId = tokenDetails?.tokenID?.toIntOrNull(),
                        chargingStationId = tokenDetails?.bookingStation?.stationId,
                        batteryCount = tokenDetails?.batteryCount
                    )
                )
            )
            autoDialerRequestUseCase.invoke(
                autoDialerRequestDTO
            ).collect(
                onLoading = {
                    _uiState.value = _uiState.value.copy(
                        bottomSheet = GetCallbackBottomSheet(
                            mobileNumber = contactNumber,
                            isLoading = true
                        )
                    )
                },
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        bottomSheet = None
                    )
                    _uiEvent.send(QrScannerUiEvent.ShowSuccessSnackbar(it.message))
                },
                onError = { errorMessage, _ ->
                    _uiEvent.send(ShowError(errorMessage))
                    _uiState.value = _uiState.value.copy(
                        bottomSheet = None
                    )
                }
            )
        }
    }


    private fun revertTokenStatus() {
        viewModelScope.launch {
            val tokenID = preferenceApi.getBookedTokenDetails()?.tokenID?.toInt()
            revertTokenCheckInStatusUseCase.invoke(
                tokenId = tokenID.orZero()
            ).collect(
                onLoading = {},
                onSuccess = {
                    val tokenDetails = preferenceApi.getBookedTokenDetails()
                    if (tokenDetails != null) {
                        preferenceApi.saveBookedTokenDetails(
                            tokenDetails.copy(
                                tokenExpiryTimeStamp = it.expiredTimeStamp.orZero()
                            )
                        )
                    }
                    _uiEvent.send(OnTokenCheckInReverted)
                },
                onError = { errorMessage, _ ->
                    _uiEvent.send(ShowError(errorMessage))
                }
            )
        }
    }

    fun isValidCode(code: String): Boolean {
        val pattern = Regex("^[Yy][NnMm][0-9][a-zA-Z][0-9]{5}$")
        return pattern.matches(code)
    }

    fun checkIsNewDiyUserStatus() {
        viewModelScope.launch {
            val isNewDiyUser = preferenceApi.getBookedTokenDetails()?.isNewDiyUser.orFalse()
            _uiState.value = _uiState.value.copy(
                showIllustrationScreen = isNewDiyUser
            )
            loggerApi.logDWithTag(
                "ScanQrViewModel",
                "isNewDiyUser: $isNewDiyUser, showIllustrationScreen: ${_uiState.value.showIllustrationScreen}"
            )
        }
    }

    fun changeShowIllustrationScreenStatus(status: Boolean) {
        _uiState.value = _uiState.value.copy(
            showIllustrationScreen = status
        )
    }

    init {
        checkIsNewDiyUserStatus()
    }

    private fun startDiyFlow(result: String) {
        viewModelScope.launch {
            val bookedTokenDetails = preferenceApi.getBookedTokenDetails()
            val userDetails = preferenceApi.getUserData()
            val request = StartDiySwapRequestDto(
                tokenId = bookedTokenDetails?.tokenID?.toInt(),
                bikeName = userDetails?.clientVehicleQrCode,
                clientCityId = userDetails?.clientCityId
            )
//            val testingRequest = StartDiySwapRequestDto(
//                tokenId = 476,
//                bikeName = "KG1A0004648",
//                clientCityId = 17
//            )

            startDiyFlowUseCase(
                request
            ).collect(
                onLoading = {},
                onSuccess = {
                    sendScanYcuCompleteEvent(true)
                    viewModelScope.launch {
                        _uiEvent.send(NavigateToSwapInProgress(result))
                    }
                },
                onError = { errorMessage, _ ->
                    viewModelScope.launch {
                        sendScanYcuCompleteEvent(false, errorMessage)
                        _uiEvent.send(ShowError(errorMessage))
                    }
                }
            )
        }

    }

    private fun sendScanYcuCompleteEvent(isSuccess: Boolean, errorMessage: String? = null) {
        viewModelScope.launch {
            val commonParams = commonAnalyticsParamsProvider.get()
            /*   analyticsApi.postEvent(
                   event = "scan_ycu_complete",
                   values = commonParams + mapOf(
                       "is_QR_scan" to uiState.value.isQrScan,
                       "status" to if (isSuccess) "success" else "failed",
                       "failure_reason" to errorMessage.orEmpty(),
                       "ycu_number" to uiState.value.scannedQrCode.orEmpty(),
                   )
               )*/
        }
    }

    fun sendScanYcuScreenViewed() {
        viewModelScope.launch {
            val commonParams = commonAnalyticsParamsProvider.get()
            /*   analyticsApi.postEvent(
                   event = "screen_viewed",
                   values = commonParams + mapOf(
                       "screen_name" to "scan_ycu_screen"
                   )
               )*/
        }
    }

    class Factory(
        private val startDiyFlowUseCase: StartDiyFlowUseCase,
        private val revertTokenCheckInStatusUseCase: RevertTokenCheckInStatusUseCase,
        private val commonAnalyticsParamsProvider: CommonAnalyticsParamsProvider,
        private val autoDialerRequestUseCase: AutoDialerRequestUseCase,
        private val preferenceApi: YumaPrefUtilApi,
        //private val analyticsApi: AnalyticsApi,
        private val loggerApi: LoggerApi
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ScanQrViewModel(
                startDiyFlowUseCase = startDiyFlowUseCase,
                revertTokenCheckInStatusUseCase = revertTokenCheckInStatusUseCase,
                commonAnalyticsParamsProvider = commonAnalyticsParamsProvider,
                autoDialerRequestUseCase = autoDialerRequestUseCase,
                preferenceApi = preferenceApi,
                loggerApi = loggerApi,
            ) as T
    }
}

sealed class QrScannerUiEvent {
    data class ShowError(val message: String) : QrScannerUiEvent()
    data class NavigateToSwapInProgress(val result: String) : QrScannerUiEvent()
    data object OnTokenCheckInReverted : QrScannerUiEvent()
    data class ShowSuccessSnackbar(val message: String) : QrScannerUiEvent()
}

