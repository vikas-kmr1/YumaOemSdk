package com.yuma.oemsdk.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yuma.oemsdk.SdkServiceLocator
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.network.AuthResult
import com.yuma.oemsdk.ui.SdkAuthFailedScreen
import com.yuma.oemsdk.ui.SdkHomeScreen
import com.yuma.oemsdk.ui.SdkLoadingScreen
import kotlinx.serialization.Serializable

// ── Route Definitions ─────────────────────────────────────────────────────────
@Serializable internal object SdkRoute {
    @Serializable object Loading
    @Serializable object Home
    @Serializable object AuthFailed
}

/**
 * Root Compose NavHost for the SDK.
 *
 * Flow:
 *  1. Starts at [SdkRoute.Loading] — initiates silent authentication.
 *  2. On success → navigates to [SdkRoute.Home] (full feature experience).
 *  3. On failure → navigates to [SdkRoute.AuthFailed] with retry/dismiss options.
 *
 * @param onDismiss Called when the user taps "Go Back" on the auth failed screen.
 *                  Typically used by [SdkLaunchActivity] to finish itself.
 */
@Composable
internal fun SdkNavHost(onDismiss: () -> Unit) {
    val navController = rememberNavController()
    var authErrorMessage by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = SdkRoute.Loading
    ) {

        // ── 1. Loading / Silent Auth ──────────────────────────────────────────
        composable<SdkRoute.Loading> {
            SdkLoadingScreen()

            LaunchedEffect(Unit) {
                val clientKey = YumaSdk.getConfig().clientKey
                val result = SdkServiceLocator.silentAuthManager.authenticate(clientKey)

                when (result) {
                    is AuthResult.Success -> {
                        // Save tokens to prefs + in-memory bearer store
                        SdkServiceLocator.prefManager.saveTokens(
                            result.accessToken,
                            result.refreshToken
                        )
                        YumaSdk.saveSessionTokens(result.accessToken, result.refreshToken)

                        navController.navigate(SdkRoute.Home) {
                            popUpTo(SdkRoute.Loading) { inclusive = true }
                        }
                    }
                    is AuthResult.Failure -> {
                        authErrorMessage = result.message
                        navController.navigate(SdkRoute.AuthFailed) {
                            popUpTo(SdkRoute.Loading) { inclusive = true }
                        }
                    }
                }
            }
        }

        // ── 2. Full Home Experience ───────────────────────────────────────────
        composable<SdkRoute.Home> {
            SdkHomeScreen(
                onSessionExpired = {
                    // Token refresh failed — clear state and show error
                    YumaSdk.resetKtorClient()
                    authErrorMessage = "Your session has expired. Please relaunch the app."
                    navController.navigate(SdkRoute.AuthFailed) {
                        popUpTo(SdkRoute.Home) { inclusive = true }
                    }
                }
            )
        }

        // ── 3. Auth Failed ────────────────────────────────────────────────────
        composable<SdkRoute.AuthFailed> {
            SdkAuthFailedScreen(
                errorMessage = authErrorMessage,
                onRetry = {
                    navController.navigate(SdkRoute.Loading) {
                        popUpTo(SdkRoute.AuthFailed) { inclusive = true }
                    }
                },
                onDismiss = onDismiss
            )
        }
    }
}
