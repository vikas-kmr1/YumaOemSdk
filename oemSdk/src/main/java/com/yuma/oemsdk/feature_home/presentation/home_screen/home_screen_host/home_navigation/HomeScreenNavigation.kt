package com.yumaoem.feature_home.presentation.home_screen.home_screen_host.home_navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.yumaoem.core.app_navigation_state.HomeScreenDestination
import com.yumaoem.core.utils.global_events.HideBottomBar
import com.yumaoem.core.utils.global_events.ShowBottomBar
import com.yumaoem.core.utils.global_events.bottom_bar_event.BottomBarEventController
import com.yumaoem.core_ui.utils.animation.defaultEnterTransition
import com.yumaoem.core_ui.utils.animation.defaultExitTransition
import com.yumaoem.core_ui.utils.animation.defaultPopEnterTransition
import com.yumaoem.core_ui.utils.animation.defaultPopExitTransition

import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.args.SwapInProgressScreenArgs

import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.event.HomeScreenEvent
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.viewmodel.HomeViewModel
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.MapScreenRoot
import com.yumaoem.feature_home.presentation.home_screen.tag_battery.TagBatteryScannerScreenRoot
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_success_screen.components.SwapSuccessScreen
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components.BookedTokenDetailsScreenRoot
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.components.TokenQrScreenRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


var currentHomeScreen: String? = null

@Composable
fun HomeScreenRoot(
    isHomeTab: Boolean,
    navigateToPaymentsTab: () -> Unit
) {}/*
    //val viewModel = koinViewModel<HomeViewModel>()
    val navController = rememberNavController()

    // Observe the current destination from the repository (via ViewModel)
    val currentDestination: HomeScreenDestination by viewModel.currentDestination.collectAsState()
    var lastDestination by remember { mutableStateOf<HomeScreenDestination?>(null) }

    // Convert HomeScreenDestination to navigation route
    val startDestination: Any = remember(currentDestination) {
        lastDestination = currentDestination
        currentDestination.toRoute()
    }

    LaunchedEffect(currentDestination) {
        // Only navigate if the new destination is different
        if (currentDestination != lastDestination) {
            lastDestination = currentDestination

            val route = currentDestination.toRoute()

            navController.navigate(route) {
                if (currentDestination == HomeScreenDestination.MapScreen ||
                    currentDestination == HomeScreenDestination.MapScreenPostSwap) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination.toString()

    LaunchedEffect(currentRoute) {
        currentHomeScreen = currentRoute

        when {
            currentRoute.contains("DiySwapInProgressScreen") ||
                    currentRoute.contains("SwapSuccessScreen") ||
                    currentRoute.contains("DiySwapStartedScreen") -> {
                hideBottomNavigationBar()
            }
            else -> {
                showBottomNavigationBar()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition
    ) {
        navigateAsPerState(
            viewModel = viewModel,
            onPurchasePlanClicked = navigateToPaymentsTab,
            isHomeTab = isHomeTab
        )
    }
}

private fun NavGraphBuilder.navigateAsPerState(
    isHomeTab:Boolean,
    viewModel: HomeViewModel,
    onPurchasePlanClicked:() -> Unit
) {

    composable<MapScreen> { backStackEntry ->
        val mapScreen: MapScreen = backStackEntry.toRoute<MapScreen>()
        MapScreenRoot(
            isHomeTab = isHomeTab,
            isPostSwap = mapScreen.isPostSwap,
            onTokenBooked = {
                viewModel.onEvent(event = HomeScreenEvent.OnTokenBooked)
            },
            onPurchasePlanClicked = onPurchasePlanClicked,
            navigateToTagBattery = {
                viewModel.onEvent(event = HomeScreenEvent.NavigateToTagBattery)
            }
        )
    }

    composable<BookedTokenDetailsScreen> {
        BookedTokenDetailsScreenRoot(
            isHomeTab = isHomeTab,
            onBookingCancelled = {
                viewModel.onEvent(event = HomeScreenEvent.OnBookingCancelled)
            },
            onCheckedInAtStation = {
                viewModel.onEvent(event = HomeScreenEvent.OnCheckedInAtStation)
            },
            onDiySwapStarted = {
                viewModel.onEvent(event = HomeScreenEvent.OnDiySwapStarted)
            }
        )
    }

    composable<TokenQrDetailsScreen> {
        TokenQrScreenRoot(
            isHomeTab = isHomeTab,
            onBookingCancelled = {
                viewModel.onEvent(event = HomeScreenEvent.OnBookingCancelled)
            },
            onSwapCompleted = {
                viewModel.onEvent(event = HomeScreenEvent.OnSwapComplete(swapTime = it))
            }
        )
    }

    composable<SwapSuccessScreen> {
        SwapSuccessScreen(
            swapTime = viewModel.swapTime.orEmpty(),
            navigateToHomeScreen = {
                viewModel.onEvent(event = HomeScreenEvent.OnSwapSuccessShown)
            }
        )
    }

    composable<ScanMachineQrScreen> {
        ScanMachineQrScreenRoot(
            isHomeTab = isHomeTab,
            navigateToSwapInProgress = {
                viewModel.swapInProgressScreenArgs = SwapInProgressScreenArgs(
                    checkInTime = 0L,
                    ycuQrCode = it,
                    isSwapInitiated = false
                )
                viewModel.onEvent(HomeScreenEvent.OnYcuQrScanned)
            },
            onTokenCheckInReverted = {
                viewModel.onEvent(event = HomeScreenEvent.OnCheckInReverted)
            }
        )
    }

    composable<DiySwapInProgressScreen> {
        if (viewModel.swapInProgressScreenArgs != null) {
            DiySwapInProgressScreenRoot(
                isHomeTab = isHomeTab,
                onRetry = {
                    viewModel.onEvent(HomeScreenEvent.NavigateToYcuScanScreen)
                },
                onSuccessfulSwap = { swapTime ->
                    viewModel.onEvent(event = HomeScreenEvent.OnSwapComplete(swapTime = swapTime))
                },
                args = viewModel.swapInProgressScreenArgs!!
            )
        }
    }

    composable<DiySwapStartedScreen> {
        DiySwapInProgressScreenRoot(
            onRetry = {
                viewModel.onEvent(HomeScreenEvent.NavigateToYcuScanScreen)
            },
            onSuccessfulSwap = { swapTime ->
                viewModel.onEvent(event = HomeScreenEvent.OnSwapComplete(swapTime = swapTime))
            },
            args = SwapInProgressScreenArgs(
                checkInTime = 0L,
                ycuQrCode = null,
                isSwapInitiated = true
            ),
            isHomeTab = isHomeTab
        )
    }

    composable<TagBatteryScannerScreen> {
        TagBatteryScannerScreenRoot(
            isHomeTab = isHomeTab,
            navigateToMapScreen = {
                viewModel.onEvent(event = HomeScreenEvent.OnBookingCancelled)
            }
        )
    }
}
*/
fun hideBottomNavigationBar() {
    val coroutineScope = CoroutineScope(Dispatchers.Main+ SupervisorJob())
    coroutineScope.launch {
        BottomBarEventController.sendEvent(HideBottomBar)
    }
}

fun showBottomNavigationBar() {
    val coroutineScope = CoroutineScope(Dispatchers.Main+ SupervisorJob())
    coroutineScope.launch {
        BottomBarEventController.sendEvent(ShowBottomBar)
    }
}