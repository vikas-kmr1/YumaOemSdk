package com.yumaoem.core_ui.utils.snackbar

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Represents a one-off event for displaying a Snackbar.
 *
 * @param message The text to show in the Snackbar.
 * @param action Optional [SnackbarAction] with a label and a callback to invoke if the user taps it.
 *
 * ## Usage
 * ```kotlin
 * // Emitting an event from a fragment
 *   lifecycleScope.launch {
 *   SnackbarController.sendEvent(
 *     SnackbarEvent(
 *       message = "Item saved",
 *       action = SnackbarAction("Undo") { viewModel.undoSave() }
 *     )
 *   )
 * }
 * ```
 *
 * @author maroof
 */

data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit
)

object SnackbarController {

    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}