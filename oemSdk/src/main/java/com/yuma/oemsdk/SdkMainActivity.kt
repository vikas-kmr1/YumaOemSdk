package com.yuma.oemsdk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.segment.analytics.BuildConfig
import com.yuma.oemsdk.navigation.SdkNavHost
import com.yumaoem.core_ui.theme.YumaAppTheme

/**
 * The SDK's internal Activity, launched when [YumaSdk.launchHome] is called.
 *
 * Responsibilities:
 * - Location + Bluetooth permissions requests
 * - Edge-to-edge status bar setup
 * - Hosts [SdkNavHost] which drives Loading → Home flow
 *
 * **Registered in the SDK's AndroidManifest — not exported.**
 * The host application never directly references this class.
 */
internal class SdkMainActivity : ComponentActivity() {

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            Log.d("SdkMainActivity", "✅ Location permission granted — updates started")
        } else {
            Log.w("SdkMainActivity", "⚠️ Location permission denied — map features may be limited")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BuildConfig.DEBUG
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb()
            )
        )

        requestLocationPermissionsIfNeeded()

        setContent {
            YumaAppTheme {
                SdkNavHost(
                    onExit = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop location updates when the SDK activity is closed


    }

    private fun requestLocationPermissionsIfNeeded() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {

        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}