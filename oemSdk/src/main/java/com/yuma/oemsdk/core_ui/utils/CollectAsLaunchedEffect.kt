package com.yumaoem.core_ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
inline fun <T> Flow<T>.collectAsLaunchedEffect(
    key: Any?,
    crossinline action: suspend (T) -> Unit
) {
    LaunchedEffect(key) {
        collect { action(it) }
    }
}