package com.yuma.oemsdk

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.yuma.oemsdk.navigation.SdkNavHost
import com.yumaoem.core.utils.global_events.EnableBluetoothEvent
import com.yumaoem.core.utils.global_events.HideKeyboard
import com.yumaoem.core.utils.global_events.ShowEnableLocationDialog
import com.yumaoem.core.utils.global_events.controller.EventController
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.utils.snackbar.ObserveAsEvents

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
internal class SdkMainActivity : ComponentActivity(), CFCheckoutResponseCallback {


    private lateinit var resolutionLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var onLocationEnabledCallback: (() -> Unit)? = null
    private var onLocationDeniedCallback: (() -> Unit)? = null

    private lateinit var bluetoothEnableLauncher: ActivityResultLauncher<Intent>
    private var onBluetoothEnabledCallback: (() -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resolutionLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                onLocationEnabledCallback?.invoke()
                onLocationEnabledCallback = null
                onLocationDeniedCallback = null
            } else {
                onLocationDeniedCallback?.invoke()
                onLocationEnabledCallback = null
                onLocationDeniedCallback = null
            }
        }

        bluetoothEnableLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                onBluetoothEnabledCallback?.invoke()
            } else {
                // User cancelled or denied enabling Bluetooth
            }
            onBluetoothEnabledCallback = null
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {

            val darkColor = Color.Transparent
            val lightColor = Color.Transparent

            val isDarkTheme = true

            enableEdgeToEdge(
                statusBarStyle = if (!isDarkTheme) {
                    SystemBarStyle.dark(darkColor.hashCode())
                } else SystemBarStyle.light(lightColor.hashCode(), lightColor.hashCode()),
                navigationBarStyle = if (!isDarkTheme) {
                    SystemBarStyle.dark(darkColor.hashCode())
                } else SystemBarStyle.light(lightColor.hashCode(), lightColor.hashCode())
            )
            App { finish() }
            ObserveGlobalEvents()
        }
        YumaSdk.andoridPaymentContextProvider.setCurrentActivity(this)
    }

    private fun checkAndPromptEnableLocation(
        context: Context,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        val client = LocationServices.getSettingsClient(context)
        client.checkLocationSettings(settingsRequest)
            .addOnSuccessListener {}
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    launcher.launch(intentSenderRequest)
                }
            }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun enableBluetooth(onEnabled: () -> Unit) {
        onBluetoothEnabledCallback = onEnabled
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            // Android 12 and below → can enable programmatically
            Log.d("MAIN_ACTIVITY", "Turning on The Bluetooth")
            val adapter = BluetoothAdapter.getDefaultAdapter() ?: return
            val success = adapter.enable()
            if (success) {
                onEnabled()
            }
        } else {
            // Android 13+ → must show system dialog
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothEnableLauncher.launch(enableBtIntent)
        }
    }

    @Composable
    fun ObserveGlobalEvents() {
        ObserveAsEvents(
            flow = EventController.events,
        ) { event ->
            when (event) {
                is ShowEnableLocationDialog -> {
                    onLocationEnabledCallback = event.onLocationEnabled
                    onLocationDeniedCallback = event.onLocationDenied
                    checkAndPromptEnableLocation(this@SdkMainActivity, resolutionLauncher)
                }

                is EnableBluetoothEvent -> {
                    onBluetoothEnabledCallback = event.onBluetoothEnabled

                    val hasPermission =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) == PackageManager.PERMISSION_GRANTED
                        } else {
                            true // Permission not required on Android < 12
                        }

                    if (hasPermission) {
                        enableBluetooth(onEnabled = {
                            event.onBluetoothEnabled?.invoke()
                        })
                    } else {
                        Log.d("MAIN_ACTIVITY", "No Permission")
                    }
                }

                is HideKeyboard -> {
                    hideKeyboard(currentFocus ?: View(this))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        YumaSdk.andoridPaymentContextProvider.clearCurrentActivity()
    }


    override fun onPaymentVerify(orderID: String?) {
        YumaSdk.paymentManager.notifySuccess(orderID.orEmpty())
    }

    override fun onPaymentFailure(cfErrorResponse: CFErrorResponse?, orderID: String?) {
        YumaSdk.paymentManager.notifyFailure(
            orderId = orderID.orEmpty(),
            code = cfErrorResponse?.code.orEmpty(),
            message = cfErrorResponse?.message.orEmpty()
        )
    }


}

@Composable
private fun App(finish: () -> Unit) {
    YumaAppTheme {
        SdkNavHost(
            onExit = { finish() }
        )
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
