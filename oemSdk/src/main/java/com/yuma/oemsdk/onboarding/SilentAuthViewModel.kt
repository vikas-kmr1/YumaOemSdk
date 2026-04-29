package com.yuma.oemsdk.onboarding

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.onboarding.utils.DropOffNavigator
import com.yumaoem.core.app_navigation_state.HomeScreenDestination
import com.yumaoem.core.app_navigation_state.NavigationStateRepository
import com.yumaoem.core.model.auth.AuthBearerTokens
import com.yumaoem.core.model.auth.User
import com.yumaoem.core.utils.app_utils.getFcmToken
import com.yumaoem.core.utils.app_utils.isLocationEnabled
import com.yumaoem.core.utils.device_info.DeviceInfoProvider
import com.yumaoem.core.utils.global_events.ShowEnableLocationDialog
import com.yumaoem.core.utils.global_events.controller.EventController
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_onboarding.data.dto.fcm_token.request.InsertFcmRequest
import com.yumaoem.feature_onboarding.data.dto.verify_otp.request.SilentAuthRequest
import com.yumaoem.feature_onboarding.data.network.OnboardingRemoteDataSource
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.SilentAuthResponse
import com.yumaoem.feature_onboarding.domain.use_case.drop_off.GetDropOffDataUseCase
import com.yumaoem.feature_onboarding.domain.use_case.verify_otp.SilentAuthUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


data class SilentAuthState(
    val isErrorState: Boolean = false,
    val errorMessage: String = "",
    val showFetchingLocationDialog: Boolean = false,
)

sealed class SilentAuthUiEvent {
    object NavigateToHomeScreen : SilentAuthUiEvent()
    data class ShowSnackbar(val message: String) : SilentAuthUiEvent()
}

internal class SilentAuthViewModel(
    private val navigationStateRepository: NavigationStateRepository,
    private val preferenceApi: YumaPrefUtilApi,
    private val silentAuthUseCase: SilentAuthUseCase,
    private val dropOffDataUseCase: GetDropOffDataUseCase,
    private val dataSource: OnboardingRemoteDataSource,
    // private val analyticsApi: AnalyticsApi,
    private val deviceInfoProvider: DeviceInfoProvider,
) : ViewModel() {


    var state by mutableStateOf(SilentAuthState())
        private set

    private val _uiEvent = MutableSharedFlow<SilentAuthUiEvent>()
    val uiEvent: SharedFlow<SilentAuthUiEvent> = _uiEvent
    private val TAG = SilentAuthViewModel::class.java.simpleName

    private val dropOffNavigator = DropOffNavigator(
        preferenceApi = preferenceApi,
        navigationStateRepository = navigationStateRepository,
        coroutineScope = viewModelScope
    )

    init {
        viewModelScope.launch {
            //if (!ensureLocationIsEnabled()) return@launch
            silentAuth()
        }
    }

    private fun silentAuth() {
        viewModelScope.launch {
            val deviceInfo = deviceInfoProvider.getDeviceInfo()
            silentAuthUseCase.invoke(
                silentAuthRequest = SilentAuthRequest(
                    clientId = 0,
                    clientKey = YumaSdk.getConfig().clientKey,
                    model = deviceInfo.model,
                    manufacturer = deviceInfo.manufacturer,
                    osName = deviceInfo.osName,
                    sdkVersion = deviceInfo.appVersion,
                    brand = deviceInfo.manufacturer, // or deviceInfo.brand if available
                    androidId = deviceInfo.androidId,
                    screenResolution = deviceInfo.screenResolution,
                    deviceType = deviceInfo.deviceType
                )
            ).collect(
                onLoading = {
                    state = state.copy(
                        isErrorState = false
                    )
                },
                onSuccess = {
                    //Save bearer tokens
                    validateUserDataAndProceed(it)
                },
                onError = { errorMessage, _ ->
                    _uiEvent.emit(SilentAuthUiEvent.ShowSnackbar(errorMessage))
                    state = state.copy(
                        isErrorState = false
                    )
                }
            )
        }

    }

    private suspend fun ensureLocationIsEnabled(): Boolean {
        if (!isLocationEnabled()) {
            EventController.sendEvent(
                ShowEnableLocationDialog(
                    onLocationEnabled = {
                        silentAuth()
                    },
                    onLocationDenied = {
                        viewModelScope.launch {
                            SnackbarController.sendEvent(
                                SnackbarEvent(
                                    message = "Turn on location to continue"
                                )
                            )
                        }
                    }
                ))
            return false
        }
        return true
    }

    private suspend fun SilentAuthViewModel.validateUserDataAndProceed(
        it: SilentAuthResponse,
    ) {
        saveUserDetails(it)
    }

    private suspend fun saveBearerTokens(it: SilentAuthResponse) {
        preferenceApi.saveBearerTokens(
            bearerTokens = AuthBearerTokens(
                accessToken = it.accessToken.token,
                refreshToken = it.refreshToken.token
            )
        )
        delay(200)
        insertFCMToken()
    }

    private suspend fun saveUserDetails(response: SilentAuthResponse) {
        val user = response.user
        if (user.clientDetails.isEmpty()) {
            _uiEvent.emit(SilentAuthUiEvent.ShowSnackbar("Client details not available"))
            return
        }
        if (user.currentClientCityIds.isEmpty()) {
            _uiEvent.emit(SilentAuthUiEvent.ShowSnackbar("Client city IDs not available"))
            return
        }

        if (user.clientVehicles.isEmpty()) {
            _uiEvent.emit(SilentAuthUiEvent.ShowSnackbar("Client vehicles not available"))
            return
        }
        if (user.activeClientUsers.isEmpty()) {
            _uiEvent.emit(SilentAuthUiEvent.ShowSnackbar("Active client users not available"))
            return
        }
        preferenceApi.saveUserData(
            user = User(
                userId = user.id,
                phone = user.phone,
                firstName = user.firstName,
                surname = user.surname,
                userProfileUrl = user.userProfileUrl,
                isPrePaidUser = user.isPrePaidUser,
                clientId = user.clientDetails[0].clientId,
                clientCityId = user.currentClientCityIds[0].clientCityId,
                clientVehicleQrCode = user.clientVehicles[0].qrCode,
                clientVehicleId = user.clientVehicles[0].clientVehicleId,
                clientUserId = user.activeClientUsers[0].clientUserId,
                clientVehicleGroupId = user.clientVehicles[0].itemGroupId,
                bikeProvider = user.clientVehicles[0].bikeProvider,
                bikeNumber = user.clientVehicles[0].bikeNumber,
                batteryCount = user.batteryCount
            )
        )
        saveBearerTokens(response)
        delay(200)
        state = state.copy(
            isErrorState = false
        )
        sendIdentifyUserEvent(user = user)
        sendLoginSuccessfulEvent(user = user)
        getUserDropOffData()
        _uiEvent.emit(SilentAuthUiEvent.NavigateToHomeScreen)
    }


    private fun insertFCMToken() {
        viewModelScope.launch {
            var token = preferenceApi.getFCMToken().firstOrNull()
            val userId: String? = preferenceApi.getUserData()?.userId
            val authToken = preferenceApi.getBearerTokens()?.accessToken
            if (token == null) {
                token = getFcmToken()
            }
            if (userId != null) {
                dataSource.saveFcmToken(
                    request = InsertFcmRequest(
                        userId = userId.toInt(),
                        fcmToken = token,
                        authToken = authToken.orEmpty()
                    )
                )
            }
        }
    }


    private fun sendIdentifyUserEvent(user: com.yumaoem.feature_onboarding.domain.model.verify_otp.response.User) {
        viewModelScope.launch {
            /*          analyticsApi.identify(
                          userId = user.id,
                          traits = mapOf(
                              "user_id" to user.id,
                              "name" to "${user.firstName} ${user.surname}",
                              "mobile_number" to user.phone,
                          )
                      )*/
        }
    }

    private fun sendLoginSuccessfulEvent(user: com.yumaoem.feature_onboarding.domain.model.verify_otp.response.User) {
        viewModelScope.launch {
            /*    analyticsApi.postEvent(
                    event = "silent_auth_successful",
                    values = mapOf(
                        "user_id" to user.id,
                        "name" to "${user.firstName} ${user.surname}",
                        "mobile_number" to user.phone
                    )
                )*/
        }
    }

    private fun getUserDropOffData() {
        viewModelScope.launch {
            val clientUserId = preferenceApi.getUserData()?.clientUserId
            clientUserId?.let {
                dropOffDataUseCase.invoke(it).collect(
                    onLoading = {
                        state = state.copy(
                            isErrorState = false
                        )
                    },
                    onSuccess = { data ->
                        dropOffNavigator.navigateToScreen(data = data)

                    },
                    onError = { _, _ ->
                        state = state.copy(
                            isErrorState = true
                        )
                        navigationStateRepository.updateHomeDestination(HomeScreenDestination.MapScreen)
                        _uiEvent.emit(SilentAuthUiEvent.NavigateToHomeScreen)
                    }
                )
            }
        }
    }


    class Factory(
        private val navigationStateRepository: NavigationStateRepository,
        private val preferenceApi: YumaPrefUtilApi,
        private val silentAuthUseCase: SilentAuthUseCase,
        private val dropOffDataUseCase: GetDropOffDataUseCase,
        private val dataSource: OnboardingRemoteDataSource,
        //private val analyticsApi: AnalyticsApi,
        private val deviceInfoProvider: DeviceInfoProvider,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SilentAuthViewModel(
                navigationStateRepository = navigationStateRepository,
                preferenceApi = preferenceApi,
                silentAuthUseCase = silentAuthUseCase,
                dropOffDataUseCase = dropOffDataUseCase,
                dataSource = dataSource,
                // analyticsApi = analyticsApi,
                deviceInfoProvider = deviceInfoProvider,
            ) as T
    }

}