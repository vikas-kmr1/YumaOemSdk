package com.yumaoem.feature_home.presentation.home_screen.home_screen_host.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumaoem.core.app_navigation_state.HomeScreenDestination
import com.yumaoem.core.app_navigation_state.NavigationStateRepository
import com.yumaoem.core.utils.orFalse
import com.yumaoem.core.utils.orZero
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.common.notification.ServiceLauncher
import com.yumaoem.feature_home.common.toDTO
import com.yumaoem.feature_home.data.dto.whatsapp_support_details.SupportDetailsRequestDto
import com.yumaoem.feature_home.domain.model.drop_off_data.DropOffScreenData
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails
import com.yumaoem.feature_home.domain.usecase.drop_off.GetDropOffDataUseCase
import com.yumaoem.feature_home.domain.usecase.support_details.GetWhatsappSupprtDetailsUseCase
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.args.SwapInProgressScreenArgs
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.event.HomeScreenEvent
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.state.HomeState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.user_current_location_provider.LocationProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class HomeViewModel(
    private val yumaPrefUtil: YumaPrefUtilApi,
    private val serviceLauncher: ServiceLauncher,
    private val supportDetailsUseCase: GetWhatsappSupprtDetailsUseCase,
    private val navigationStateRepository: NavigationStateRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    val currentDestination: StateFlow<HomeScreenDestination> = navigationStateRepository.currentHomeDestination

    var swapTime:String? = ""

    var swapInProgressScreenArgs: SwapInProgressScreenArgs? = null

    private fun updateHomeScreenDestination(destination: HomeScreenDestination) {
        viewModelScope.launch {
            if (destination == HomeScreenDestination.MapScreen ||
                destination == HomeScreenDestination.MapScreenPostSwap) {
                yumaPrefUtil.removeBookedTokenDetails()
            }

            // Update through repository
            navigationStateRepository.updateHomeDestination(destination)
        }

        viewModelScope.launch {
            delay(500)
            getCustomerSupportData()
        }
    }

    private fun getCustomerSupportData() {
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
                    yumaPrefUtil.saveSupportDetails(it.phoneNumber, it.defaultMessage)
                },
                onError = { _, _ -> }
            )
        }
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            HomeScreenEvent.OnCheckedInAtStation -> {
                updateHomeScreenDestination(HomeScreenDestination.TokenQrDetailsScreen)
                serviceLauncher.stopForeGroundNotification()
            }

            HomeScreenEvent.OnTokenBooked -> {
                updateHomeScreenDestination(HomeScreenDestination.BookedTokenDetailsScreen)
            }

            // when booking is cancelled or token is expired
            HomeScreenEvent.OnBookingCancelled -> {
                updateHomeScreenDestination(HomeScreenDestination.MapScreen)
                serviceLauncher.stopForeGroundNotification()
            }

            is HomeScreenEvent.OnSwapComplete -> {
                swapTime = event.swapTime
                updateHomeScreenDestination(HomeScreenDestination.SwapSuccessScreen)
            }

            HomeScreenEvent.OnSwapSuccessShown -> {
                updateHomeScreenDestination(HomeScreenDestination.MapScreenPostSwap)
            }

            HomeScreenEvent.OnDiySwapStarted -> {
                updateHomeScreenDestination(HomeScreenDestination.ScanMachineQrScreen)
                serviceLauncher.stopForeGroundNotification()
            }

            HomeScreenEvent.OnYcuQrScanned -> {
                updateHomeScreenDestination(HomeScreenDestination.DiySwapInProgressScreen)
            }

            HomeScreenEvent.NavigateToYcuScanScreen -> {
                updateHomeScreenDestination(HomeScreenDestination.ScanMachineQrScreen)
            }

            HomeScreenEvent.OnCheckInReverted -> {
                updateHomeScreenDestination(HomeScreenDestination.BookedTokenDetailsScreen)
            }

            HomeScreenEvent.NavigateToTagBattery -> {
                updateHomeScreenDestination(HomeScreenDestination.TagBatteryScannerScreen)
            }
        }
    }
    init {
        getCustomerSupportData()
    }
    companion object {
        const val CHECK_IN_TOKEN_SCREEN = "CHECK_IN_TOKEN"
        const val MAP_SCREEN = "CHARGING_STATION_LIST"
        const val SWAP_IN_PROGRESS_SCREEN = "SWAP_INPROGRESS_TOKEN"
        const val   DIY_SWAP_INITIATED = "DIY_SWAP_INITIATED"
    }
}