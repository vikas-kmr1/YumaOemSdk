package com.yumaoem.core.utils.handle_permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PermissionsHandlerViewModel(
    private val controller: PermissionsController,
) : ViewModel() {

    // Keep track of each permission’s current state
    private val _permissionStates = mutableMapOf<Permission, MutableStateFlow<PermissionState?>>()
    // Expose immutable StateFlow for UI to observe
    fun getPermissionStateFlow(permission: Permission): StateFlow<PermissionState?> {
        val flow = _permissionStates.getOrPut(permission) { MutableStateFlow(null) }
        return flow.asStateFlow()
    }

    /**
     * 1) Check all permissions in [list].
     * 2) If any are not yet Granted, request exactly those “missing” ones.
     * 3) In the end, invoke [onAllPermissionsResult] with `true` if and only if
     *    all permissions are Granted; otherwise `false`.
     */
    fun checkAndRequestAllPermissions(
        list: List<Permission>,
        onAllPermissionsResult: (permissionState: RequestedPermissionState) -> Unit
    ) {
        viewModelScope.launch {
            val missingPermissions = mutableListOf<Permission>()

            // Update permission states and collect missing ones
            for (permission in list) {
                val currentState = controller.getPermissionState(permission)
                val flow = _permissionStates.getOrPut(permission) { MutableStateFlow(null) }
                flow.value = currentState

                if (currentState != PermissionState.Granted) {
                    missingPermissions.add(permission)
                }
            }

            // If all permissions are already granted
            if (missingPermissions.isEmpty()) {
                onAllPermissionsResult(RequestedPermissionState.Granted)
                return@launch
            }

            // Request each missing permission
            for ((index, permission) in missingPermissions.withIndex()) {
                request(permission) { newState ->
                    _permissionStates[permission]?.value = newState

                    val isLastPermission = index == missingPermissions.lastIndex
                    if (!isLastPermission) return@request

                    // After last permission is handled
                    val allGranted = areAllPermissionsGranted(list)

                    if (allGranted) {
                        onAllPermissionsResult(RequestedPermissionState.Granted)
                        return@request
                    }

                    val deniedPermissions = list.filter {
                        _permissionStates[it]?.value == PermissionState.Denied
                    }

                    val deniedAlwaysPermissions = list.filter {
                        _permissionStates[it]?.value == PermissionState.DeniedAlways
                    }

                    // Retry temporarily denied permissions
                    if (deniedPermissions.isNotEmpty()) {
                        for ((retryIndex, deniedPermission) in deniedPermissions.withIndex()) {
                            request(deniedPermission) { retryState ->
                                _permissionStates[deniedPermission]?.value = retryState

                                val isLastRetry = retryIndex == deniedPermissions.lastIndex
                                if (isLastRetry) {
                                    if (areAllPermissionsGranted(list)) {
                                        onAllPermissionsResult(RequestedPermissionState.Granted)
                                    } else {
                                        onAllPermissionsResult(RequestedPermissionState.DeniedAlways(formatPermissionsList(list)))
                                    }
                                }
                            }
                        }
                    } else if (deniedAlwaysPermissions.isNotEmpty()) {
                        onAllPermissionsResult(RequestedPermissionState.DeniedAlways(formatPermissionsList(deniedAlwaysPermissions)))
                    }
                }
            }
        }

    }

    private fun areAllPermissionsGranted(list: List<Permission>) =
        list.all { perm ->
            _permissionStates[perm]?.value == PermissionState.Granted
        }

    /**
     * Helper: requests a single [permission], then invokes [onResult] with the final state.
     * This will suspend until the user either grants, denies, or “never ask again.”
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
                // User dismissed the dialog without choosing
                onResult(PermissionState.NotDetermined)
            }
        }
    }
}

sealed class RequestedPermissionState {
    data object Granted : RequestedPermissionState()
    data class DeniedAlways(val permissions : String) : RequestedPermissionState()
}

fun formatPermissionsList(permissions: List<Permission>): String {
    return when (permissions.size) {
        0 -> ""
        1 -> permissions[0].toString()
        2 -> "${permissions[0]} & ${permissions[1]}"
        else -> {
            val allButLast = permissions.dropLast(1).joinToString(", ")
            val last = permissions.last().toString()
            "$allButLast & $last"
        }
    }
}


