package com.yuma.oemsdk.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.onboarding.SilentAuthUiEvent
import com.yuma.oemsdk.onboarding.SilentAuthViewModel
import com.yumaoem.core_network.impl.util.NetworkApiEvent
import com.yumaoem.core_network.impl.util.NetworkEventBus
import com.yumaoem.core_ui.utils.snackbar.ObserveAsEvents
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.bottom_nav.HomeScreenHost
import kotlinx.serialization.Serializable


@Serializable
object SilentAuthRoute


@Serializable
object HomeScreenRoute

@Composable
internal fun SdkNavHost(modifier: Modifier, onExit: () -> Unit) {
    val navController = rememberNavController()

    var onboardingStart by remember { mutableStateOf<Any>(SilentAuthRoute) }

    ObserveAsEvents(
        flow = NetworkEventBus.INSTANCE.events
    ) {
        when (it) {
            NetworkApiEvent.REFRESH_TOKEN_EXPIRED -> {
                if (navController.currentDestination != SilentAuthRoute) {
                    onboardingStart = SilentAuthRoute
                    YumaSdk.prefManager.logoutUser()
                    onExit()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = onboardingStart
    ) {

        composable<SilentAuthRoute> {
            val viewModel: SilentAuthViewModel = viewModel(
                factory = YumaSdk.silentAuthViewModelFactory
            )
            LaunchedEffect(viewModel) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is SilentAuthUiEvent.NavigateToHomeScreen -> {
                            navController.navigate(HomeScreenRoute) {
                                popUpTo(SilentAuthRoute) { inclusive = true }
                            }
                        }

                        is SilentAuthUiEvent.ShowSnackbar -> {
                            SnackbarController.sendEvent(
                                event = SnackbarEvent(
                                    message = event.message,
                                )
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.LightGray,
                    trackColor = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        composable<HomeScreenRoute> {
            HomeScreenHost(
                onUserLoggedOut = {
                    YumaSdk.onReset()
                    onExit()
                },
                exitSdk = onExit
            )
        }
    }
}

