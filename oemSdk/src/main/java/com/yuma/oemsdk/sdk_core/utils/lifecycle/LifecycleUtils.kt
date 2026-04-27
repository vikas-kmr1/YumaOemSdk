package com.yumaoem.core.utils.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * A Composable that observes lifecycle events from a [LifecycleOwner]
 * and invokes the corresponding lambda callbacks.
 *
 * This function uses a [DisposableEffect] to add a [LifecycleEventObserver]
 * when the composable enters the composition and removes it on disposal.
 * This is useful for triggering actions in response to lifecycle changes from within
 * a Composable function.
 *
 * @param lifecycleOwner The [LifecycleOwner] whose lifecycle is to be observed.
 * @param onCreate Lambda to be executed when the [Lifecycle.Event.ON_CREATE] event is received.
 * @param onStart Lambda to be executed when the [Lifecycle.Event.ON_START] event is received.
 * @param onResume Lambda to be executed when the [Lifecycle.Event.ON_RESUME] event is received.
 * @param onPause Lambda to be executed when the [Lifecycle.Event.ON_PAUSE] event is received.
 * @param onStop Lambda to be executed when the [Lifecycle.Event.ON_STOP] event is received.
 * @param onDestroy Lambda to be executed when the [Lifecycle.Event.ON_DESTROY] event is received.
 * @param onAny Lambda to be executed for any lifecycle event.
 */
@Composable
fun GetLifecycleEvents(
    lifecycleOwner: LifecycleOwner,
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {},
    onAny: () -> Unit = {}
) {

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> onCreate()
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_PAUSE -> onPause()
                Lifecycle.Event.ON_STOP -> onStop()
                Lifecycle.Event.ON_DESTROY -> onDestroy()
                Lifecycle.Event.ON_ANY -> onAny()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}
