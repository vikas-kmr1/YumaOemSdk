package com.yumaoem.core_ui.utils.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Observes a cold [Flow] of one-off events in a @Composable and delivers them
 * to [onEvent] whenever the lifecycle is at least STARTED.
 *
 * ## Usage
 * ```kotlin
 * @Composable
 * fun MyScreen(viewModel: MyViewModel) {
 *   ObserveAsEvents(viewModel.navigationEvents) { event ->
 *     // Handle your event here, e.g. navigate or show a Snackbar
 *   }
 * }
 *
 * @param flow The Flow emitting events to collect.
 * @param key1 Optional restart key for the underlying LaunchedEffect.
 * @param key2 Optional second restart key for the underlying LaunchedEffect.
 * @param onEvent Callback invoked for each value emitted by [flow].
 *
 * @author
 * maroof
 */

@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle, key1, key2, flow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
