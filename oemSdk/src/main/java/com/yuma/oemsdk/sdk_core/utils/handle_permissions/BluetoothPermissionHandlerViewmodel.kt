package com.yumaoem.core.utils.handle_permissions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_CONNECT
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_LE
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_SCAN
import dev.icerock.moko.permissions.location.LOCATION

import kotlinx.coroutines.launch


/**
 * ViewModel responsible for handling location permission using the [PermissionsController].
 *
 * This ViewModel observes and updates the [PermissionState] for the Bluetooth permission.
 * It provides a method to request the permission and updates the internal state accordingly.
 *
 * ## Usage:
 * This ViewModel can be used in your UI to:
 * - Check the current state of the permission
 * - Request permission if needed
 * - Reactively update UI based on the [bluetoothPermissionState] value
 *
 * @param controller A platform-specific [PermissionsController] that handles the permission logic.
 * @author
 * Maroof Ansari
 */

class HomeScreenPermissionViewModel(
    private val controller: PermissionsController,
) : ViewModel() {

    var bluetoothLeState by mutableStateOf(PermissionState.NotDetermined)
        private set

    var bluetoothScanState by mutableStateOf(PermissionState.NotDetermined)
        private set

    var bluetoothConnectState by mutableStateOf(PermissionState.NotDetermined)
        private set

    var locationPermissionState by mutableStateOf(PermissionState.NotDetermined)
        private set

    init {
        viewModelScope.launch {
            bluetoothLeState   = controller.getPermissionState(Permission.BLUETOOTH_LE)
            bluetoothScanState = controller.getPermissionState(Permission.BLUETOOTH_SCAN)

           // locationPermissionState = controller.getPermissionState(Permission.LOCATION)
        }
    }

    /**
     * Checks or requests a permission and updates the corresponding state.
     */
    private fun request(
        permission: Permission,
        onResult: (PermissionState) -> Unit
    ) {
        viewModelScope.launch {
            try {
                controller.providePermission(permission)
                onResult(PermissionState.Granted)
            } catch (e: DeniedAlwaysException) {
                onResult(PermissionState.DeniedAlways)
            } catch (e: DeniedException) {
                onResult(PermissionState.Denied)
            } catch (e: RequestCanceledException) {
                // user dismissed system dialog
                onResult(PermissionState.NotDetermined)
            }
        }
    }

    fun requestBluetoothConnect() =
        request(Permission.BLUETOOTH_CONNECT) { bluetoothConnectState = it }

    fun requestBluetoothLe() =
        request(Permission.BLUETOOTH_LE) { bluetoothLeState = it }

    fun requestBluetoothScan() =
        request(Permission.BLUETOOTH_SCAN) { bluetoothScanState = it }

    fun requestLocationPermission() = request(Permission.LOCATION) { locationPermissionState = it }
}
