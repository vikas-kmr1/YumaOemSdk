package com.yuma.oemsdk.navigation

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            LinearProgressIndicator()
        }

        // ── 2. Full Home Experience ───────────────────────────────────────────
        composable<SdkRoute.Home> {
           Text("Home")
        }

        // ── 3. Auth Failed ────────────────────────────────────────────────────
        composable<SdkRoute.AuthFailed> {
          Text("Auth Failed")
        }
    }
}
