package com.yumaoem.core.utils.global_events

/**
 * A sealed interface representing one-time events that can be passed around the app.
 * These are typically UI-triggered or system-driven instructions that shouldn't persist.
 */
sealed interface AppEvent

/**
 * Triggers a system prompt to enable location services.
 */
data class ShowEnableLocationDialog(
    val onLocationEnabled: () -> Unit,
    val onLocationDenied: () -> Unit
) : AppEvent

data class EnableBluetoothEvent(
    val onBluetoothEnabled: () -> Unit
): AppEvent

data object HideBottomBar : AppEvent
data object ShowBottomBar : AppEvent

/**
 * Event to trigger a navigation action.
 */
data class NavigateTo(val destination: String) : AppEvent

/**
 * Event to show a toast.
 */
data class ShowToast(val message: String) : AppEvent

/**
 * Event to show a dialog.
 */
data class ShowDialog(val title: String, val description: String) : AppEvent

/**
 * Event to Hide Keyboard.
 */
data object HideKeyboard : AppEvent

/**
 * Register SMS reader to listen for incoming SMS.
 */
data class RegisterSMSReader(
    val onOTPReceived: (String) -> Unit
) : AppEvent
data object UnregisterSMSReader : AppEvent

