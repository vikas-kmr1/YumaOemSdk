package com.yumaoem.feature_home.presentation.home_screen.home_screen_host.home_navigation

import com.yumaoem.core.app_navigation_state.HomeScreenDestination
import kotlinx.serialization.Serializable

sealed interface HomeRoute
@Serializable data class MapScreen(val isPostSwap: Boolean = false) : HomeRoute
@Serializable object BookedTokenDetailsScreen : HomeRoute
@Serializable object TokenQrDetailsScreen : HomeRoute
@Serializable object SwapSuccessScreen : HomeRoute
@Serializable object ScanMachineQrScreen : HomeRoute
@Serializable object DiySwapInProgressScreen : HomeRoute
@Serializable object DiySwapStartedScreen : HomeRoute
@Serializable object TagBatteryScannerScreen : HomeRoute


fun HomeScreenDestination.toRoute(): HomeRoute = when (this) {
    HomeScreenDestination.MapScreen -> MapScreen(isPostSwap = false)
    HomeScreenDestination.MapScreenPostSwap -> MapScreen(isPostSwap = true)
    HomeScreenDestination.BookedTokenDetailsScreen -> BookedTokenDetailsScreen
    HomeScreenDestination.TokenQrDetailsScreen -> TokenQrDetailsScreen
    HomeScreenDestination.SwapSuccessScreen -> SwapSuccessScreen
    HomeScreenDestination.ScanMachineQrScreen -> ScanMachineQrScreen
    HomeScreenDestination.DiySwapInProgressScreen -> DiySwapInProgressScreen
    HomeScreenDestination.DiySwapStartedScreen -> DiySwapStartedScreen
    HomeScreenDestination.TagBatteryScannerScreen -> TagBatteryScannerScreen
}