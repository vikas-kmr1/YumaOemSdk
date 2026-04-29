package com.yuma.oemsdk.navigation

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.onboarding.SilentAuthUiEvent
import com.yuma.oemsdk.onboarding.SilentAuthViewModel
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.bottom_nav.HomeScreenHost
import kotlinx.serialization.Serializable


@Serializable
object SilentAuthRoute


@Serializable
object HomeScreenRoute

@Composable
internal fun SdkNavHost(onExit: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current

    var onboardingStart by remember { mutableStateOf<Any>(SilentAuthRoute) }

//    ObserveAsEvents(
//        flow = NetworkEventBus.INSTANCE.events
//    ) {
//        when (it) {
//            NetworkApiEvent.REFRESH_TOKEN_EXPIRED -> {
//                if(navController.currentDestination != SilentAuth){
//                    onboardingStart = LoginScreen
//                    navController.navigate(Onboarding){
//                        yumaPrefUtilApi.logoutUser()
//                        popUpTo(HomeScreen) { inclusive = true }
//                    }
//                }
//            }
//        }
//    }

    NavHost(
        navController = navController,
        startDestination = onboardingStart
    ) {

        composable<SilentAuthRoute> {
            val viewModel: SilentAuthViewModel = viewModel(
                factory = YumaSdk.silentAuthViewModelFactory!!
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

            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

        composable<HomeScreenRoute> {
            HomeScreenHost(
                onUserLoggedOut = {
                    onboardingStart = SilentAuthRoute
                    (context as Activity).finish()
                },
                exitSdk = onExit
            )
        }
    }
}

