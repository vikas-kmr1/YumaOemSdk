/*
package com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuma.oemsdk.data.network.SdkHomeRemoteDataSource
import com.yuma.oemsdk.location.SdkLocationManager
import com.yuma.oemsdk.prefs.SdkPrefManager
import com.yuma.oemsdk.viewmodel.SdkHomeViewModel
import com.yumacustomer.core_analytics.api.AnalyticsApi
import com.yumacustomer.core_logger.api.LoggerApi
import com.yumacustomer.new_ble_sdk.data.CommonSessionConfig
import com.yumacustomer.new_ble_sdk.data.SmartSwapSubmitResponse
import com.yumacustomer.new_ble_sdk.data.YumaResponse
import com.yumaoem.core.model.auth.User
import com.yumaoem.core.utils.currentTimeMillis
import com.yumaoem.core.utils.orZero
import com.yumaoem.core.utils.qr_validator.BatteryQrValidator
import com.yumaoem.core.utils.qr_validator.QR_Patterns.BATTERY_CODE_SEPARATOR
import com.yumaoem.core.utils.vibration.vibrate
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.corepreference.model.BookedTokenDetailsDTO
import com.yumaoem.feature_home.common.customer_support.CustomerSupportCallInteractor
import com.yumaoem.feature_home.common.util.analytics_utils.CommonAnalyticsParamsProvider
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerDetail
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerRequest
import com.yumaoem.feature_home.data.dto.cu_response.CUResponseDTO
import com.yumaoem.feature_home.data.dto.cu_response.deserializeCUResponse
import com.yumaoem.feature_home.domain.usecase.auto_dialer.AutoDialerRequestUseCase
import com.yumaoem.feature_home.domain.usecase.ble.CleanupBleSessionUseCase
import com.yumaoem.feature_home.domain.usecase.ble.InitializeBleSessionUseCase
import com.yumaoem.feature_home.domain.usecase.ble.ObserveBleResponsesUseCase
import com.yumaoem.feature_home.domain.usecase.ble.SmartSwapSubmitUseCase
import com.yumaoem.feature_home.domain.usecase.ble.StartSwapUseCase
import com.yumaoem.feature_home.domain.usecase.ble.SubmitChargedBatteryQrUseCase
import com.yumaoem.feature_home.domain.usecase.ble.SubmitSwapResultUseCase
import com.yumaoem.feature_home.domain.usecase.ble.SwapStatusUseCase
import com.yumaoem.feature_home.domain.usecase.get_battery_details.GetBatteryDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.profile_screen.GetUserDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.token_status.GetTokenStatusUseCase
import com.yumaoem.feature_home.presentation.diy_flow.CommonSessionConfigFactory
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.args.SwapInProgressScreenArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class DiySwapInProgressViewModel(
    private val initializeBleSessionUseCase: InitializeBleSessionUseCase,
    private val startSwapUseCase: StartSwapUseCase,
    private val swapStatusUseCase: SwapStatusUseCase,
    private val submitSwapResultUseCase: SubmitSwapResultUseCase,
    private val cleanupBleSessionUseCase: CleanupBleSessionUseCase,
    private val observeResponses: ObserveBleResponsesUseCase,
    private val commonSessionConfigFactory: CommonSessionConfigFactory,
    private val getTokenStatusUseCase: GetTokenStatusUseCase,
    private val autoDialerRequestUseCase: AutoDialerRequestUseCase,
    private val submitChargedBatteryQrUseCase: SubmitChargedBatteryQrUseCase,
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
    private val smartSwapSubmitUseCase: SmartSwapSubmitUseCase,
    private val customerSupportCallInteractor: CustomerSupportCallInteractor,
    private val commonAnalyticsParamsProvider: CommonAnalyticsParamsProvider,
    private val prefsApi: YumaPrefUtilApi,
    private val analyticsApi: AnalyticsApi,
    private val loggerApi: LoggerApi,
    private val json: Json
) : ViewModel() {

    companion object {
        const val TAG = "DiySwapInProgressViewModel"
    }

    private val _state = MutableStateFlow(DiySwapInProgressState())
    val state: StateFlow<DiySwapInProgressState> = _state

    private val _uiEvent = Channel<DiySwapInProgressUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var submitJob: Job? = null
    private var launchedTimeStamp = 0L
    private var manualBatteryLaunchedTimeStamp = 0L

    private fun observeBleResponses() {
        viewModelScope.launch {
            observeResponses().collect { response ->
                handleBleResponse(response)
            }
        }
    }

    fun setSwapInProgressArgs(
        args: SwapInProgressScreenArgs
    ) {
        launchedTimeStamp = currentTimeMillis()
        loggerApi.logDWithTag(TAG, "setSwapInProgressArgs: $args")
        viewModelScope.launch {
            if (args.isSwapInitiated) {
                _state.value = _state.value.copy(
                    isSwapInitiated = true
                )
                continueSession(checkInTime = args.checkInTime)
            } else {
                _state.value = _state.value.copy(
                    checkedInTime = args.checkInTime,
                    ycuQrCode = args.ycuQrCode!!
                )
                delay(100)
                startSwap(
                    args.checkInTime,
                    args.ycuQrCode
                )
            }
        }
    }

    */
/**
     * Handles the BLE response flow:
     * 1. Initialization: Receives [YumaResponse.InitSuccess].
     * 2. Swap Process:
     *    - Door opens, user drops discharged battery.
     *    - Calls [computeSwapDifference] on submit.
     * 3. Verification:
     *    - Receives [YumaResponse.SyncDifferenceRes].
     *    - Verifies if the count of picked and dropped batteries matches the swap count.
     * 4. Completion:
     *    - Calls [SubmitSwapResultUseCase] if verification passes.
     *    - On [YumaResponse.SubmitSuccess], calls [CleanupBleSessionUseCase].
     *
     * Note: Refer to DiyViewModel in Yuma app for SDK response handling details.
     *//*


    private fun handleBleResponse(response: YumaResponse) {
        loggerApi.logDWithTag(TAG, "handleBleResponse: ${response}")
        when (response) {
            is YumaResponse.NetworkSuccess -> handleNetworkSuccess()
            is YumaResponse.Connected -> handleConnected()
            is YumaResponse.SyncDifferenceRes -> handleSyncDifference(response)
            is YumaResponse.SubmitSuccess -> handleSubmitSuccess()
            is YumaResponse.Error -> handleError(response)
            is YumaResponse.PermissionGranted -> handlePermissionGranted()
            is YumaResponse.InitSuccess -> handleInitSuccess()
            is YumaResponse.ResponseState -> {
                loggerApi.logDWithTag(TAG, "ResponseState received: ${response.cuResponse}")
                handleResponseState(response)
                _state.update { it.copy(status = "Response state received") }
            }
            is YumaResponse.ConfigSet -> handleConfigSet()
            else -> {
                _state.update { it.copy(status = "Status: $response") }
            }
        }
    }

    private fun handleNetworkSuccess() {
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false,
                status = "Network initialized, discovering devices...",
                error = null,
                bottomSheet = DiySwapBottomSheet.None
            )
        }
    }

    private fun handleConnected() {
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = true,
                status = "Connected to YCU device",
                error = null,
                bottomSheet = DiySwapBottomSheet.None
            )
        }
    }

    private fun handleSyncDifference(response: YumaResponse.SyncDifferenceRes) {
        */
/** this logic is shelved**//*

    }

    private fun handleSubmitSuccess() {
        submitJob?.cancel()
        submitJob = null

        viewModelScope.launch {
            delay(100)
            cleanupSession()
            getSwapTime()
        }
        sendSubmitClickedEvent(true, null)
        sendDiySwapInProgressScreenViewedEvent(true)
        if(_state.value.showScanBatteryScreen){
            sendManualBatteryScannedEvent(true, null, _state.value.isManualEntry)
            sendManualBatteryScreenViewedEvent(isFinalEvent = true)
        }
        _state.update {
            it.copy(
                isSubmitting = false,
                submitSuccess = true,
                status = "Swap submitted successfully",
                error = null,
                bottomSheet = DiySwapBottomSheet.None,
            )
        }
    }

    private fun handleError(response: YumaResponse.Error) {
        when (response.code) {
            YumaResponse.ERROR_SUBMIT_FAILED -> handleSubmitFailedError(response.message, response.id ?: 0)
            YumaResponse.ERROR_NETWORK_ERROR -> handleNetworkError(response.message)
            YumaResponse.ERROR_DISCONNECTED -> handleDisconnectedError(response.message)
            YumaResponse.ERROR_PERMISSION_NOT_GRANTED -> handlePermissionNotGrantedError(response.message)
            YumaResponse.ERROR_ACCESS_TYPE_FAILED -> handleAccessTypeFailedError(response.message)
            YumaResponse.ERROR_CONNECTION_FAILED -> handleConnectionFailedError(response.message)
            YumaResponse.ERROR_QUICK_SCANNING_FAILED -> handleQuickScanningFailedError(response.message)
            YumaResponse.ERROR_SYSTEM_SYNC_FAILED -> handleSystemSyncFailedError(response.message)
            YumaResponse.ERROR_SWAP_UNAVAILABLE -> handleSwapUnavailableError(response.message)
            else -> handleGenericError(response.code, response.message)
        }
    }

    private fun handleSubmitFailedError(message: String, id: Int) {
        sendSubmitClickedEvent(false, message)
        submitJob?.cancel()
        submitJob = null
        if(_state.value.showScanBatteryScreen){
            sendManualBatteryScannedEvent(false, message, _state.value.isManualEntry)
        }
        if(id == 0) {
            showSubmitFailedBottomSheet()
            _state.update {
                it.copy(
                    isSubmitting = false,
                    error = message,
                )
            }
            return
        }
        */
/** taking all other values as default except id and message **//*

        val smartSwapResponse = SmartSwapSubmitResponse(
                id = id,
                message = message,
                isManualFlowEnabled = false,
                timeTaken = "",
                isTokenCompleted = false,
                isSessionTimedOut = false,
                isCallInitiated = false,
                isPingAvailable = false,
                isYcuScanAllowedAgain = false
        )
        handleSmartSwapSubmitSuccess(smartSwapResponse)
    }

    private fun handleNetworkError(message: String) {
        showSomethingWentWrongBottomSheet("Network error: $message")
        _state.update {
            it.copy(
                isConnecting = false,
                error = message,
            )
        }
    }

    private fun handleDisconnectedError(message: String) {
        _state.update {
            it.copy(
                isConnected = false,
                status = "Disconnected from YCU device",
                error = message,
                bottomSheet = if (it.isConnecting) {
                    DiySwapBottomSheet.BluetoothConnectionFailed
                } else {
                    DiySwapBottomSheet.None
                }
            )
        }
    }

    private fun handlePermissionNotGrantedError(message: String) {
        _state.update {
            it.copy(
                isConnecting = false,
                status = "Required permissions not granted",
                error = message,
                bottomSheet = DiySwapBottomSheet.BluetoothConnectionFailed
            )
        }
    }

    private fun handleAccessTypeFailedError(message: String) {
        showSomethingWentWrongBottomSheet("AccessTypeFailed")
        _state.update {
            it.copy(
                isConnecting = false,
                status = "AccessTypeFailed",
                error = message,
            )
        }
    }

    private fun handleConnectionFailedError(message: String) {
        cleanupSession()
        _state.update {
            it.copy(
                isConnecting = false,
                status = "ConnectionFailed",
                error = message,
                bottomSheet = DiySwapBottomSheet.BluetoothConnectionFailed
            )
        }
    }

    private fun handleQuickScanningFailedError(message: String) {
        showSomethingWentWrongBottomSheet("QuickScanningFailed")
        _state.update {
            it.copy(
                isConnecting = false,
                status = "QuickScanningFailed",
                error = message,
            )
        }
    }

    private fun handleSystemSyncFailedError(message: String) {
        showSomethingWentWrongBottomSheet("SystemSyncFailed")
        _state.update {
            it.copy(
                isConnecting = false,
                status = "SystemSyncFailed",
                error = message,
            )
        }
    }

    private fun handleSwapUnavailableError(message: String) {
        showScanAnotherMachineBottomSheet("Swap unavailable: $message")
        _state.update {
            it.copy(
                isConnecting = false,
            )
        }
    }

    private fun handleGenericError(code: String, message: String) {
        showSomethingWentWrongBottomSheet("Error: $message")
        _state.update {
            it.copy(
                isConnecting = false,
                status = "Error: $code",
                error = message,
            )
        }
    }

    private fun handlePermissionGranted() {
        _state.update {
            it.copy(
                isConnecting = true,
                status = "PermissionGranted",
            )
        }
    }

    private fun handleInitSuccess() {
        _state.update {
            it.copy(
                isSubmitButtonVisible = true
            )
        }
    }

    private fun handleConfigSet() {
        loggerApi.logDWithTag(TAG, "handleConfigSet: ${_state.value.isSwapInitiated}")
        if(_state.value.isSwapInitiated) {
            stopSwap()
            _state.update {
                it.copy(
                    status = "ConfigSet",
                    isSubmitButtonVisible = true
                )
            }
        }else{
            sdkStartSwap()
        }
    }

    private fun handleSmartSwapSubmitSuccess(response: SmartSwapSubmitResponse) {
        sendSubmitClickedEvent(true, null)
        loggerApi.logDWithTag(TAG, "handleSmartSwapSubmitSuccess: $response")
        when(response.id) {
            1,2,4 -> {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        showScanBatteryScreen = true,
                    )
                }
            }
            3,10 -> {
                viewModelScope.launch {
                    _uiEvent.send(DiySwapInProgressUiEvent.SwapCompleted(response.timeTaken))
                }
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        submitSuccess = true,
                        status = "Swap submitted successfully",
                        error = null,
                        bottomSheet = DiySwapBottomSheet.None,
                    )
                }
            }
            5 -> {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        bottomSheet = DiySwapBottomSheet.DBInsertFailed
                    )
                }
            }
            6 -> {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        bottomSheet = DiySwapBottomSheet.DBInsertFailedCustomerSupport
                    )
                }
            }
            7 -> {
                if(response.isCallInitiated){
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            bottomSheet = DiySwapBottomSheet.RemoteSwapInProgress
                        )
                    }
                }else{
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            bottomSheet = DiySwapBottomSheet.CBOpenFailed
                        )
                    }
                }
            }
            8,9 -> {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        bottomSheet = DiySwapBottomSheet.SwapStatusNotCompleted
                    )
                }
            }
            else -> {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        bottomSheet = DiySwapBottomSheet.None
                    )
                }
            }
        }
    }

    private fun showSubmitFailedBottomSheet() {
        if (_state.value.isSwapInitiated){
            _state.update {
                it.copy(
                    bottomSheet = DiySwapBottomSheet.SubmitFailureScanAgain,
                    isBatteryScanEnabled = false,
                )
            }
        }
    }

    fun showCustomerSupportBottomSheet() {
        viewModelScope.launch {
            val mobileNumber = prefsApi.getUserData()?.phone
            _state.value = _state.value.copy(
                bottomSheet = DiySwapBottomSheet.GetCallbackBottomSheet(
                    mobileNumber = mobileNumber.orEmpty(),
                    isLoading = false
                )
            )
        }
    }

    fun showSomethingWentWrongBottomSheet(
        error: String
    ) {
        _state.value = _state.value.copy(
            isConnecting = false,
            error = error,
            status = "Error",
            bottomSheet = DiySwapBottomSheet.SomethingWentWrong
        )
        cleanupSession()
    }


    fun showScanAnotherMachineBottomSheet(
        error: String
    ) {
        _state.value = _state.value.copy(
            isConnecting = false,
            error = error,
            status = "Swap Unavailable",
            bottomSheet = DiySwapBottomSheet.NoChargedBatteryFound
        )
        cleanupSession()
    }

    // will give init success on initialization
    fun initializeSession(sessionConfig: CommonSessionConfig, enableAnalytics: Boolean = true) {
        loggerApi.logDWithTag(TAG, "initializeSession: ${sessionConfig}")
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isConnecting = false,
                    error = null,
                    status = "Initializing...",
                    bottomSheet = DiySwapBottomSheet.None
                )
                cleanupBleSessionUseCase()
                initializeBleSessionUseCase(sessionConfig, enableAnalytics)
                observeBleResponses()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isConnecting = false,
                    error = e.message,
                    status = "Initialization failed: ${e.message}",
                    bottomSheet = DiySwapBottomSheet.SomethingWentWrong
                )
                cleanupSession()
            }
        }
    }


    fun checkSwapStatus(tokenId : Long) {
        */
/** logic shelved **//*

    }

    fun sdkStartSwap() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isConnecting = true,
                    error = null,
                    status = "sdk starting swap...",
                    bottomSheet = DiySwapBottomSheet.None
                )
                startSwapUseCase(_state.value.ycuQrCode)
            }catch (e: Exception) {
                _state.value = _state.value.copy(
                    isConnecting = false,
                    error = e.message,
                    status = "Connection failed: ${e.message}",
                    bottomSheet = DiySwapBottomSheet.BluetoothConnectionFailed
                )
                cleanupSession()
            }
        }
    }

    fun onSubmitClicked() {
        if (_state.value.isSwapInitiated) {
            smartSwapSubmit()
            return
        }
        if (!_state.value.isConnected) {
            smartSwapSubmit()
            return
        }
        */
/** At this point -swap is Not initiated and device is connected. **//*

        submitSwapResult()

        _state.value = _state.value.copy(isSubmitting = true)

        submitJob?.cancel()
        submitJob = viewModelScope.launch {
            delay(10_000L)
            _state.value = _state.value.copy(isSubmitting = false)
        }
    }


    fun smartSwapSubmit() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isSubmitting = true,
                    error = null,
                    status = "Submitting...",
                    bottomSheet = DiySwapBottomSheet.None
                )
               val smartSwapSubmitResponse =  smartSwapSubmitUseCase()
                handleSmartSwapSubmitSuccess(smartSwapSubmitResponse)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    error = e.message,
                    status = "Submit failed: ${e.message}",
                    bottomSheet = DiySwapBottomSheet.SomethingWentWrong
                )
            }
        }
    }

    fun submitSwapResult() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isSubmitting = true,
                    error = null,
                    status = "Submitting...",
                    bottomSheet = DiySwapBottomSheet.None
                )
                submitSwapResultUseCase()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    error = e.message,
                    status = "Submit failed: ${e.message}",
                    bottomSheet = DiySwapBottomSheet.SomethingWentWrong
                )
            }
        }
    }

    fun cleanupSession() {
        viewModelScope.launch {
            try {
                cleanupBleSessionUseCase()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    bottomSheet = DiySwapBottomSheet.SomethingWentWrong
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(
            error = null,
            bottomSheet = DiySwapBottomSheet.None
        )
    }

    fun dismissBottomSheet() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                bottomSheet = DiySwapBottomSheet.None
            )
        }
    }

    fun startSwap(checkInTime: Long, ycuQrCode: String) {
        launchSession(
            qrCode = ycuQrCode,
            checkInTime = checkInTime
        )
    }

    fun continueSession(checkInTime: Long) {
        launchSession(
            qrCode = "",
            checkInTime = checkInTime
        )
    }

    private fun launchSession(
        qrCode: String,
        checkInTime: Long
    ) {
        viewModelScope.launch {
            val userData = prefsApi.getUserData()
            val bookedToken = prefsApi.getBookedTokenDetails()

            if (bookedToken==null) {
                val errorMessage = "Token Details not found"
                sendError(errorMessage)
                showSomethingWentWrongBottomSheet(errorMessage)
                sendFailedToLoadStationDetailsEvents()
                return@launch
            }

            val bikeQrCode = userData?.clientVehicleQrCode.orEmpty()
            val batteryCount = bookedToken.batteryCount

            _state.value = _state.value.copy(
                swapInProgress = true,
                batteryCount = batteryCount
            )

            val sessionConfig = commonSessionConfigFactory.create(
                qrCode = qrCode,
                bikeName = bikeQrCode,
                checkInTime = checkInTime,
                tokenId = bookedToken.tokenID.toLong(),
                batteryCount = batteryCount,
                batteryType = bookedToken.batteryType
            )

            initializeSession(sessionConfig)
        }
    }

    fun stopSwap() {
        _state.value = _state.value.copy(
            swapInProgress = false,
            isSubmitButtonVisible = true
        )
    }

    fun getSwapTime() {
        viewModelScope.launch {
            delay(500)
            val tokenID = prefsApi.getBookedTokenDetails()?.tokenID?.toInt()
            getTokenStatusUseCase.invoke(tokenID.orZero())
                .collect(
                    onLoading = {},
                    onSuccess = { tokenStatus ->
                        when (tokenStatus.tokenStatusId) {
                            3 -> {
                                viewModelScope.launch {
                                    _uiEvent.send(DiySwapInProgressUiEvent.SwapCompleted(tokenStatus.swapTime))
                                }
                            }
                        }
                    },
                    onError = { _, _ ->
                        _uiEvent.send(DiySwapInProgressUiEvent.SwapCompleted("0"))
                    }
                )
        }
    }

    private fun handleResponseState(response: YumaResponse.ResponseState) {
        try {
            val jsonString = response.cuResponse
            loggerApi.logDWithTag(TAG, "Parsing JSON response: $jsonString")

            val cuResponse = deserializeCUResponse(jsonString, json)

            when (cuResponse) {
                is CUResponseDTO.AccessTypeDTO -> {
                    loggerApi.logDWithTag(TAG, "AccessType response: ${cuResponse.response}")
                }

                is CUResponseDTO.SystemSyncDTO -> {
                    loggerApi.logDWithTag(
                        TAG,
                        "SystemSync response with ${cuResponse.numPorts} ports"
                    )
                }

                is CUResponseDTO.UnknownTypeDTO -> {
                    loggerApi.logDWithTag(TAG, "Unknown response type: ${cuResponse.type}")
                }

                null -> {
                    loggerApi.logDWithTag(TAG, "Failed to parse CU response")
                }
            }
        } catch (e: Exception) {
            loggerApi.logDWithTag(TAG, "Failed to parse response: ${e.message}")
        }
    }

    fun requestCall(contactNumber: String) {
        viewModelScope.launch {
            customerSupportCallInteractor
                .requestCall(contactNumber)
                .collect(
                    onLoading = {
                        _state.value = _state.value.copy(
                            bottomSheet = DiySwapBottomSheet.GetCallbackBottomSheet(
                                mobileNumber = contactNumber,
                                isLoading = true
                            )
                        )
                    },
                    onSuccess = {
                        _state.value = _state.value.copy(
                            bottomSheet = DiySwapBottomSheet.None
                        )
                        _uiEvent.send(
                            DiySwapInProgressUiEvent.ShowSuccessSnackbar(it.message)
                        )
                    },
                    onError = { errorMessage, _ ->
                        viewModelScope.launch {
                            SnackbarController.sendEvent(
                                SnackbarEvent(message = errorMessage)
                            )
                        }
                        _state.value = _state.value.copy(
                            bottomSheet = DiySwapBottomSheet.None
                        )
                    }
                )
        }
    }

    fun onEvent(event: DiySwapInProgressEvent) {
        when (event) {

            DiySwapInProgressEvent.OnBackClicked -> {
                _state.update {
                    it.copy(showScanBatteryScreen = false)
                }
            }

            DiySwapInProgressEvent.OnSubmitClicked -> {
                onSubmitClicked()
            }

            is DiySwapInProgressEvent.OnBatteryScanned -> {
                onBatteryScanned(event)
                _state.update {
                    it.copy(
                        isManualEntry = event.isManualEntry
                    )
                }
            }

            DiySwapInProgressEvent.ToggleFlashlight -> {
                _state.update {
                    it.copy(isFlashlightOn = !it.isFlashlightOn)
                }
            }

            DiySwapInProgressEvent.RetryBatteryScan -> {
                _state.update {
                    it.copy(
                        isBatteryScanEnabled = true,
                        bottomSheet = DiySwapBottomSheet.None,
                        batteryQrList = emptyList()
                    )
                }
            }

            DiySwapInProgressEvent.ToggleBikeDetailsBottomSheet -> {
                _state.update {
                    it.copy(
                        showBikeDetailsBottomSheet = !it.showBikeDetailsBottomSheet
                    )
                }
            }
        }
    }

    private fun onBatteryScanned(event: DiySwapInProgressEvent.OnBatteryScanned) {
        loggerApi.logDWithTag("DiySwapInProgressViewModel", "onBatteryScanned: ${event.batteryQr}, isSubmitting: ${_state.value.isSubmitting}")
        if (!_state.value.isSubmitting) {
            validateAndStoreBatteryQr(event.batteryQr)
        }
    }


    private fun validateAndStoreBatteryQr(batteryQr: String) {
        loggerApi.logDWithTag("DiySwapInProgressViewModel", "validateAndStoreBatteryQr: $batteryQr, isSubmitting: ${_state.value.isSubmitting}, isBatteryScanEnabled: ${_state.value.isBatteryScanEnabled}")
        if (batteryQr.isEmpty() || _state.value.isSubmitting || _state.value.isBatteryScanEnabled.not()) return

        val isValid = BatteryQrValidator.validateBatteryQr(input = batteryQr)
        val alreadyExists = state.value.batteryQrList.contains(batteryQr)

        when {
            !isValid -> {
                sendManualBatteryScannedEvent(false, "Invalid QR Code", _state.value.isManualEntry)
                sendError("Invalid QR Code")
            }

            alreadyExists -> {
                sendManualBatteryScannedEvent(false, "Battery already scanned", _state.value.isManualEntry)
                sendError("Battery already scanned")
            }

            else -> {
                saveBatteryQr(batteryQr)
            }
        }
    }

    private fun saveBatteryQr(batteryQr: String) {
        val newQr = batteryQr.substringBefore(BATTERY_CODE_SEPARATOR)

        _state.update { state ->
            if (newQr in state.batteryQrList) return@update state

            vibrate(durationMillis = 150)

            val updatedList = state.batteryQrList + newQr

            if (updatedList.size == state.batteryCount) {
                submitChargedBatteryQr(updatedList)
            }

            state.copy(batteryQrList = updatedList)
        }
    }


    private fun submitChargedBatteryQr(list: List<String>) {
        viewModelScope.launch {
            if (!state.value.isSubmitting && _state.value.isBatteryScanEnabled){
                _state.update {
                    it.copy(
                        isSubmitting = true,
                        isBatteryScanEnabled = false
                    )
                }
                submitChargedBatteryQrUseCase(list)
            }
        }
    }

    private fun sendError(message: String) {
        viewModelScope.launch {
            _uiEvent.send(DiySwapInProgressUiEvent.ShowError(message))
        }
    }


    init {
        loadBookedTokenDetails()
        loadBatteryDetails()
    }

    private fun loadBatteryDetails() {
        viewModelScope.launch {

            val userDetails = getUserDetailsUseCase()

            updateBikeDetails {
                copy(
                    bikeProvider = userDetails?.bikeProvider.orEmpty(),
                    bikeNumber = userDetails?.bikeNumber.orEmpty(),
                    bikeQrNumber = userDetails?.clientVehicleQrCode.orEmpty()
                )
            }

            val clientId = userDetails?.clientVehicleId.orZero()

            getBatteryDetailsUseCase(clientVehicleId = clientId)
                .collect(
                    onLoading = {},
                    onSuccess = { batteries ->
                        updateBikeDetails {
                            copy(batteryDetails = batteries)
                        }
                    },
                    onError = { errorMessage, _ -> }
                )
        }
    }



    private inline fun updateBikeDetails(
        crossinline block: BikeDetails.() -> BikeDetails
    ) {
        _state.update { state ->
            state.copy(bikeDetails = state.bikeDetails.block())
        }
    }


    private fun loadBookedTokenDetails() {
        viewModelScope.launch {
            val tokenDetails = withContext(Dispatchers.IO) {
                prefsApi.getBookedTokenDetails()
            } ?: return@launch

            loggerApi.logDWithTag(TAG,
                    "loadBookedTokenDetails: stationID${tokenDetails.bookingStation?.stationId} StationName ${tokenDetails.bookingStation?.stationName}")

            _state.update { current ->
                current.copy(
                    stationName = tokenDetails.bookingStation?.stationName.orEmpty(),
                    stationId = tokenDetails.bookingStation?.stationId.orZero().toString()
                )
            }
        }
    }

    private fun sendFailedToLoadStationDetailsEvents() {
        viewModelScope.launch {
            val currentUser = prefsApi.getUserData()
            analyticsApi.postEvent(
                event = "FAILED_TO_GET_STATION_DETAILS",
                values = mapOf(
                    "user_id" to currentUser?.userId.orEmpty(),
                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
                    "mobile_number" to currentUser?.phone.orEmpty(),
                    "client_id" to currentUser?.clientId.orZero(),
                )
            )
        }
    }

    fun sendDiySwapInProgressScreenViewedEvent(isFinalEvent: Boolean = false) {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
            val bookedTokenDetails = prefsApi.getBookedTokenDetails()

            val eventValues = mutableMapOf<String, String>(
                "screen_name" to "diy_swap_started_screen",
                "is_diy" to bookedTokenDetails?.isDiyToken.toString(),
            )

            if (isFinalEvent) {
                eventValues["time_on_page"] =
                    (currentTimeMillis() - launchedTimeStamp).toString()
            }
            analyticsApi.postEvent(
                event = "screen_viewed",
                values = commonValues + eventValues
            )
        }
    }

    fun sendBottomSheetClickedEvent() {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
            analyticsApi.postEvent(
                event = "screen_viewed",
                values = commonValues + mapOf(
                    "screen_name" to "diy_bike_details_sheet"
                )
            )
        }
    }

    fun sendManualBatteryScreenViewedEvent(isFinalEvent: Boolean = false) {
        if(!isFinalEvent){ manualBatteryLaunchedTimeStamp = currentTimeMillis() }

        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()

            val eventValues = mutableMapOf<String, String>(
                "screen_name" to "manual_battery_screen"
            )

            if (isFinalEvent) {
                eventValues["time_on_page"] =
                    (currentTimeMillis() - manualBatteryLaunchedTimeStamp).toString()
            }
            analyticsApi.postEvent(
                event = "screen_viewed",
                values = commonValues + eventValues
            )
        }
    }

    fun sendCsButtonClickedEvent() {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
            analyticsApi.postEvent(
                event = "cs_clicked",
                values = commonValues + mapOf(
                    "screen_name" to "diy_swap_started_screen",
                    "ycu_number" to _state.value.ycuQrCode
                )
            )
        }
    }

    fun sendCsReceiveCallEvent(contactNumber: String) {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
            analyticsApi.postEvent(
                event = "cs_receive_call_clicked",
                values = commonValues + mapOf(
                    "screen_name" to "diy_swap_started_screen",
                    "call_back_number_entered" to contactNumber
                )
            )
        }
    }

    fun sendSubmitClickedEvent(isSuccess: Boolean, message: String?) {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
            analyticsApi.postEvent(
                event = "diy_swap_submit_clicked",
                values = commonValues + mapOf(
                    "status" to (if (isSuccess) "success" else "failed"),
                    "failure_reason" to (message ?: ""),
                )
            )
        }
    }

    fun sendManualBatteryScannedEvent(isSuccess: Boolean, message: String?, isManualEntry: Boolean) {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
            analyticsApi.postEvent(
                event = "manual_battery_scanned",
                values = commonValues + mapOf(
                    "battery_qr_code" to _state.value.batteryQrList.joinToString(","),
                    "status" to (if (isSuccess) "success" else "failed"),
                    "failure_reason" to (message ?: ""),
                    "ycu_number" to _state.value.ycuQrCode,
                    "is_QR_scan" to !isManualEntry,
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        cleanupSession()
    }

    internal class Factory(
        private val initializeBleSessionUseCase: InitializeBleSessionUseCase,
        private val startSwapUseCase: StartSwapUseCase,
        private val swapStatusUseCase: SwapStatusUseCase,
        private val submitSwapResultUseCase: SubmitSwapResultUseCase,
        private val cleanupBleSessionUseCase: CleanupBleSessionUseCase,
        private val observeResponses: ObserveBleResponsesUseCase,
        private val commonSessionConfigFactory: CommonSessionConfigFactory,
        private val getTokenStatusUseCase: GetTokenStatusUseCase,
        private val autoDialerRequestUseCase: AutoDialerRequestUseCase,
        private val submitChargedBatteryQrUseCase: SubmitChargedBatteryQrUseCase,
        private val getUserDetailsUseCase: GetUserDetailsUseCase,
        private val getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
        private val smartSwapSubmitUseCase: SmartSwapSubmitUseCase,
        private val customerSupportCallInteractor: CustomerSupportCallInteractor,
        private val commonAnalyticsParamsProvider: CommonAnalyticsParamsProvider,
        private val prefsApi: YumaPrefUtilApi,
        private val analyticsApi: AnalyticsApi,
        private val loggerApi: LoggerApi,
        private val json: Json
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DiySwapInProgressViewModel(
                initializeBleSessionUseCase = initializeBleSessionUseCase,
                startSwapUseCase = startSwapUseCase,
                swapStatusUseCase = swapStatusUseCase,
                submitSwapResultUseCase = submitSwapResultUseCase,
                cleanupBleSessionUseCase = cleanupBleSessionUseCase,
                observeResponses = observeResponses,
                commonSessionConfigFactory = commonSessionConfigFactory,
                getTokenStatusUseCase = getTokenStatusUseCase,
                autoDialerRequestUseCase = autoDialerRequestUseCase,
                submitChargedBatteryQrUseCase = submitChargedBatteryQrUseCase,
                getUserDetailsUseCase = getUserDetailsUseCase,
                getBatteryDetailsUseCase = getBatteryDetailsUseCase,
                smartSwapSubmitUseCase = smartSwapSubmitUseCase,
                customerSupportCallInteractor = customerSupportCallInteractor,
                commonAnalyticsParamsProvider = commonAnalyticsParamsProvider,
                prefsApi = prefsApi,
                analyticsApi = analyticsApi,
                loggerApi = loggerApi,
                json = json,
            ) as T
    }
}

sealed class DiySwapInProgressUiEvent {
    data class SwapCompleted(val swapTime: String) : DiySwapInProgressUiEvent()
    data class ShowSuccessSnackbar(val message: String) : DiySwapInProgressUiEvent()
    data class ShowError(val message: String) : DiySwapInProgressUiEvent()
}*/
