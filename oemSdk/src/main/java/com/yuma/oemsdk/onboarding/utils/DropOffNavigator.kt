package com.yuma.oemsdk.onboarding.utils

import com.yumaoem.core.app_navigation_state.HomeScreenDestination
import com.yumaoem.core.app_navigation_state.NavigationStateRepository
import com.yumaoem.core.utils.orFalse
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.common.toDTO
import com.yumaoem.feature_home.domain.model.drop_off_data.DropOffScreenData
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.viewmodel.HomeViewModel.Companion.CHECK_IN_TOKEN_SCREEN
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.viewmodel.HomeViewModel.Companion.DIY_SWAP_INITIATED
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.viewmodel.HomeViewModel.Companion.MAP_SCREEN
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.viewmodel.HomeViewModel.Companion.SWAP_IN_PROGRESS_SCREEN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DropOffNavigator(
    private val preferenceApi: YumaPrefUtilApi,
    private val navigationStateRepository: NavigationStateRepository,
    private val coroutineScope: CoroutineScope
) {

    fun updateUserData(data: DropOffScreenData) {
        coroutineScope.launch {
            val userData = preferenceApi.getUserData()
            val vehicle = data.vehicles!![0]
            val batteryCount = data.batteryCount
            if (userData!=null) {
                val updatedUserData = userData.copy(
                    clientVehicleQrCode = vehicle.qrCode,
                    clientVehicleId = vehicle.clientVehicleId,
                    clientVehicleGroupId = vehicle.itemGroupId,
                    bikeProvider = vehicle.bikeProvider,
                    bikeNumber = vehicle.bikeNumber,
                    batteryCount = batteryCount
                )
                preferenceApi.saveUserData(updatedUserData)
            }
        }
    }

    fun saveToken(token: BookedTokenDetails?) {
        token?.let {
            coroutineScope.launch {
                preferenceApi.saveBookedTokenDetails(it.toDTO())
            }
        }
    }

    fun setPrepaidUserStatus(isPrePaidUser:Boolean) {
        coroutineScope.launch {
            preferenceApi.setB2cStatus(isPrePaidUser)
        }
    }

    fun navigateToScreen(data: DropOffScreenData) {
        setPrepaidUserStatus(data.isPrePaidUser)

        if (data.vehicles!=null && data.vehicles.isNullOrEmpty().not()) {
            updateUserData(data)
        }

        if (data.tokenDetails!=null) {
            saveToken(data.tokenDetails)
        }

        when (data.screen) {
            CHECK_IN_TOKEN_SCREEN -> {
                navigationStateRepository.updateHomeDestination(HomeScreenDestination.BookedTokenDetailsScreen)
            }

            MAP_SCREEN -> {
                navigationStateRepository.updateHomeDestination(HomeScreenDestination.MapScreen)
            }

            SWAP_IN_PROGRESS_SCREEN -> {
                val isDiy = data.tokenDetails?.isDiyToken.orFalse()
                if (isDiy) {
                    navigationStateRepository.updateHomeDestination(
                        HomeScreenDestination.ScanMachineQrScreen
                    )
                } else {
                    navigationStateRepository.updateHomeDestination(
                        HomeScreenDestination.TokenQrDetailsScreen
                    )
                }
            }

            DIY_SWAP_INITIATED -> {
                navigationStateRepository.updateHomeDestination(
                    HomeScreenDestination.DiySwapStartedScreen
                )
            }
        }
    }
}
