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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.yuma.oemsdk.navigation.SdkNavHost
import com.yuma.oemsdk.onboarding.SplashScreenRoot
import com.yumaoem.core.utils.app_utils.isLocationEnabled
import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core.utils.global_events.EnableBluetoothEvent
import com.yumaoem.core.utils.global_events.HideKeyboard
import com.yumaoem.core.utils.global_events.ShowEnableLocationDialog
import com.yumaoem.core.utils.global_events.controller.EventController
import com.yumaoem.core.utils.handle_permissions.PermissionsHandlerViewModel
import com.yumaoem.core.utils.handle_permissions.RequestedPermissionState
import com.yumaoem.core.utils.lifecycle.GetLifecycleEvents
import com.yumaoem.core.utils.network_connection.ConnectionStatus
import com.yumaoem.core.utils.network_connection.NetworkStatusProvider
import com.yumaoem.core_ui.components.permission_denied_dlalog.OpenSettingsDialog
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.utils.snackbar.ObserveAsEvents
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.core_ui.utils.snackbar.composables.ErrorSnackBar
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_ADVERTISE
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_CONNECT
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_LE
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_SCAN
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.location.LOCATION
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import kotlinx.coroutines.launch

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
    val isPermissionsGranted = remember { mutableStateOf(false) }
    YumaAppTheme {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HandlePermissions(
                onPermissionsGranted = { isPermissionsGranted.value = true }
            )
            ObserveNetworkStatus()
            if (isPermissionsGranted.value) {
                StartLocationRequests()
            }
            val snackbarHostState = remember {
                SnackbarHostState()
            }

            ObserveSnackBarEvents(snackbarHostState)
            if (isPermissionsGranted.value) {
                Scaffold(
                    containerColor = Color.White,
                    snackbarHost = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                snackbar = { data ->
                                    ErrorSnackBar(data)
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { inertPadding ->
                    SdkNavHost(
                        modifier = Modifier.padding(inertPadding),
                        onExit = { finish() }
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()){
                    SplashScreenRoot()
                }
            }
        }
        GetLifecycleEvents(
            lifecycleOwner = LocalLifecycleOwner.current,
            onResume = {

            }
        )
    }
}


@Composable
private fun HandlePermissions(
    requiredPermissions: List<Permission> = listOf(
        Permission.LOCATION,
        Permission.BLUETOOTH_SCAN,
        Permission.BLUETOOTH_LE,
        Permission.BLUETOOTH_CONNECT,
        Permission.BLUETOOTH_ADVERTISE,
        Permission.REMOTE_NOTIFICATION
    ),
    onPermissionsGranted: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)
    val permissionViewModel = viewModel {
        PermissionsHandlerViewModel(controller)
    }


    val showPermissionDialog = remember { mutableStateOf(false) }
    var dialogTitle = remember { "" }

    GetLifecycleEvents(
        lifecycleOwner = LocalLifecycleOwner.current,
        onStart = {
            permissionViewModel.checkAndRequestAllPermissions(requiredPermissions) { permissionState ->
                when (permissionState) {
                    RequestedPermissionState.Granted -> {
                        showPermissionDialog.value = false
                        if (isLocationEnabled().not()) {
                            coroutineScope.launch {
                                EventController.sendEvent(
                                    ShowEnableLocationDialog(
                                    onLocationEnabled = {
                                        onPermissionsGranted()
                                    },
                                    onLocationDenied = {
                                        coroutineScope.launch {
                                            SnackbarController.sendEvent(
                                                SnackbarEvent(
                                                    message = "Turn on location to continue"
                                                )
                                            )
                                        }
                                    }
                                ))
                            }
                        } else {
                            onPermissionsGranted()
                        }
                    }

                    is RequestedPermissionState.DeniedAlways -> {
                        dialogTitle = "please grant ${permissionState.permissions} to continue"
                        showPermissionDialog.value = true
                    }
                }
            }
        })

    if (showPermissionDialog.value) {
        OpenSettingsDialog(
            title = dialogTitle,
            onDismissRequest = {
                showPermissionDialog.value = false
            },
            onOpenSettingsClicked = {
                controller.openAppSettings()
            }
        )
    }
}


@Composable
private fun ObserveSnackBarEvents(
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    ObserveAsEvents(
        flow = SnackbarController.events,
        snackbarHostState
    ) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }
}

@Composable
private fun StartLocationRequests() {
    val coroutineScope = rememberCoroutineScope()
    val locationProvider: CoreLocationProvider = YumaSdk.coreLocationProvider
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            locationProvider.startLocationUpdates()
        }
    }
}

@Composable
private fun ObserveNetworkStatus() {
    val coroutineScope = rememberCoroutineScope()
    val networkStatusProvider: NetworkStatusProvider = YumaSdk.networkStatusProvider

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            networkStatusProvider.currentConnectionStatusState.collect { connection ->
                when (connection) {
                    ConnectionStatus.NONE -> {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = "No internet connection. Please check your network.",
                                duration = SnackbarDuration.Indefinite
                            )
                        )
                    }

                    ConnectionStatus.WIFI -> {}
                    ConnectionStatus.CELLULAR -> {}
                }
            }
        }
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
