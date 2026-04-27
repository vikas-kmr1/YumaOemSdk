package com.yumaoem.core.utils.kmm_flow_util.handle_permissions

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
import dev.icerock.moko.permissions.location.LOCATION
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling location permission using the [PermissionsController].
 *
 * This ViewModel observes and updates the [PermissionState] for the LOCATION permission.
 * It provides a method to request the permission and updates the internal state accordingly.
 *
 * ## Usage:
 * This ViewModel can be used in your UI to:
 * - Check the current state of the LOCATION permission
 * - Request permission if needed
 * - Reactively update UI based on the [state] value
 *
 * @param controller A platform-specific [PermissionsController] that handles the permission logic.
 * @author
 * Maroof Ansari
 */

class PermissionHandlerViewmodel(
    private val controller: PermissionsController,
): ViewModel() {
    var state by mutableStateOf(PermissionState.NotDetermined)
        private set

    init {
        getCurrentStatePermission()
    }

    fun getCurrentStatePermission() {
        viewModelScope.launch {
            state = controller.getPermissionState(Permission.LOCATION)
        }
    }

    fun provideOrRequestLocationPermission() {
        viewModelScope.launch {
            delay(2000)
            try {
                controller.providePermission(Permission.LOCATION)
                state = PermissionState.Granted
            } catch(e: DeniedAlwaysException) {
                state = PermissionState.DeniedAlways
            } catch(e: DeniedException) {
                state = PermissionState.Denied
            } catch(e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }
}