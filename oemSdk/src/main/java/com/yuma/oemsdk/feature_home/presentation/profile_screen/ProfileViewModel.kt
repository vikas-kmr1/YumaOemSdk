package com.yumaoem.feature_home.presentation.profile_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yuma.oemsdk.YumaSdk
import com.yumacustomer.core_logger.api.LoggerApi
import com.yumaoem.core.utils.orZero
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.common.paginator.DefaultPaginator
import com.yumaoem.feature_home.common.util.analytics_utils.CommonAnalyticsParamsProvider
import com.yumaoem.feature_home.data.dto.fcm_token.request.RemoveFcmTokenRequestDto
import com.yumaoem.feature_home.data.dto.logout.LogoutUserRequestDTO
import com.yumaoem.feature_home.data.dto.swap_history.request.SwapHistoryRequest
import com.yumaoem.feature_home.data.network.HomeRemoteDataSource
import com.yumaoem.feature_home.domain.model.profile.UserDetails
import com.yumaoem.feature_home.domain.usecase.get_battery_details.GetBatteryDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.logout_user.LogoutUserUseCase
import com.yumaoem.feature_home.domain.usecase.profile_screen.GetSwapHistoryUseCase
import com.yumaoem.feature_home.domain.usecase.profile_screen.GetUserDetailsUseCase
import com.yumaoem.feature_home.presentation.profile_screen.components.UiSwapItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val getSwapHistoryUseCase: GetSwapHistoryUseCase,
    private val getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
    private val commonAnalyticsParamsProvider: CommonAnalyticsParamsProvider,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val dataSource: HomeRemoteDataSource,
    private val prefUtilApi: YumaPrefUtilApi,
    //private val analyticsApi: AnalyticsApi,
    private val loggerApi: LoggerApi,
) : ViewModel() {

    var state by mutableStateOf(ProfileScreenState())
        private set

    private val _uiEvent = Channel<ProfileScreenUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val paginator = DefaultPaginator(
        initialKey = state.page,
        onLoadUpdated = {
            state = state.copy(isRefreshing = it)
        },
        onRequest = { nextPage ->
            getSwapHistoryUseCase.invoke(
                swapHistoryRequest = SwapHistoryRequest(
                    clientVehicleId = state.user?.clientVehicleId.orZero(),
                    page = nextPage,
                    limit = 10
                )
            )
        },
        getNextKey = {
            state.page + 1
        },
        onError = {
            _uiEvent.send(ProfileScreenUiEvent.ShowError(it))
        },
        onSuccess = { items, newKey ->
            val uiItems = items.flatMap { history ->
                listOf(UiSwapItem.DateHeader(history.date)) +
                        history.swaps.map { UiSwapItem.SwapItem(it) }
            }
            state = state.copy(
                swapHistory = state.swapHistory + uiItems,
                page = newKey,
                endReached = items.isEmpty()
            )
        }
    )

    fun onLogout() {
        viewModelScope.launch {
            sendUserLogoutEvent()
            prefUtilApi.logoutUser()
            YumaSdk.onReset()
            val userId: String? = prefUtilApi.getUserData()?.userId
            val refreshToken: String = prefUtilApi.getBearerTokens()?.refreshToken.orEmpty()
        }
    }

    private fun getUserDetails() {
        state = state.copy(
            userDetails = UserDetails(
                fullName = "${state.user?.firstName} ${state.user?.surname}",
                mobileNumber = "+91-${state.user?.phone.orEmpty()}",
                profileImageUrl = "https://yuma-static.s3.ap-south-1.amazonaws.com/oem/image.png",
                bikeProvider = state.user?.bikeProvider.orEmpty(),
                bikeNumber = state.user?.bikeNumber.orEmpty(),
                bikeQrNumber = state.user?.clientVehicleQrCode.orEmpty()
            )
        )
    }

    fun toggleBottomSheet() {
        loggerApi.logDWithTag("ProfileViewModel", "toggleBottomSheet")
        val bottomSheet = state.isSheetOpen
        if (bottomSheet.not()) {
            sendProfileScreenQrClickEvent()
        }
        state = state.copy(
            isSheetOpen = !bottomSheet
        )
    }


    fun isProfileTab() {
        viewModelScope.launch {
            updateUserData()
            loadNextItems()
            loadBatteryDetails()
        }
    }

    init {
        updateUserData()
    }

    private fun updateUserData() {
        viewModelScope.launch {
            val user = getUserDetailsUseCase()
            state = state.copy(user = user)
            getUserDetails()
        }
    }

    private fun loadBatteryDetails() {
        viewModelScope.launch {
            val clientId = state.user?.clientVehicleId.orZero()
            getBatteryDetailsUseCase.invoke(
                clientVehicleId = clientId
            ).collect(
                onLoading = {},
                onSuccess = {
                    state = state.copy(
                        batteryDetails = it
                    )
                },
                onError = { errorMessage, _ ->
                    println("ProfileViewModel: $errorMessage")
                }
            )
        }
    }

    fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun sendProfileScreenLaunchedEvent() {
        viewModelScope.launch {
            val commonValues = commonAnalyticsParamsProvider.get()
//            analyticsApi.postEvent(
//                event = "screen_viewed",
//                values = commonValues + mapOf(
//                    "screen_name" to "profile_screen_viewed"
//                )
//            )
        }
    }

    fun sendProfileScreenQrClickEvent() {
        viewModelScope.launch {
            val currentUser = prefUtilApi.getUserData()
//            analyticsApi.postEvent(
//                event = "profile_screen_qr_clicked",
//                values = mapOf(
//                    "user_id" to currentUser?.userId.orEmpty(),
//                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
//                    "bikeProvider" to state.user?.bikeProvider.orEmpty(),
//                    "bikeNumber" to state.user?.bikeNumber.orEmpty(),
//                )
//            )
        }
    }

    fun sendUserLogoutEvent() {
        viewModelScope.launch {
            val currentUser = prefUtilApi.getUserData()
//            analyticsApi.postEvent(
//                event = "user_logged_out",
//                values = mapOf(
//                    "user_id" to currentUser?.userId.orEmpty(),
//                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
//                    "mobile_number" to currentUser?.phone.orEmpty(),
//                    "type" to "manual"
//                )
//            )
        }
    }

    class Factory(
        private val getUserDetailsUseCase: GetUserDetailsUseCase,
        private val getSwapHistoryUseCase: GetSwapHistoryUseCase,
        private val getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
        private val commonAnalyticsParamsProvider: CommonAnalyticsParamsProvider,
        private val logoutUserUseCase: LogoutUserUseCase,
        private val dataSource: HomeRemoteDataSource,
        private val prefUtilApi: YumaPrefUtilApi,
        //private val analyticsApi: AnalyticsApi,
        private val loggerApi: LoggerApi,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(
                getUserDetailsUseCase = getUserDetailsUseCase,
                getSwapHistoryUseCase = getSwapHistoryUseCase,
                getBatteryDetailsUseCase = getBatteryDetailsUseCase,
                commonAnalyticsParamsProvider = commonAnalyticsParamsProvider,
                logoutUserUseCase = logoutUserUseCase,
                dataSource = dataSource,
                prefUtilApi = prefUtilApi,
                loggerApi = loggerApi,
                ) as T
    }

}

sealed class ProfileScreenUiEvent {
    data class ShowError(val message: String) : ProfileScreenUiEvent()
    data object UserLoggedOut : ProfileScreenUiEvent()
}
