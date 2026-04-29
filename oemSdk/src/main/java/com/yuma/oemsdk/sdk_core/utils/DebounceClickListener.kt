package com.yumaoem.core.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize

inline fun Modifier.debounceClickable(
    enabled: Boolean = true,
    debounceInterval: Long = 1000L,
    crossinline onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by rememberSaveable { mutableStateOf(0L) }
    clickable(enabled = enabled) {
        val currentTime = currentTimeMillis()
        if ((currentTime - lastClickTime) < debounceInterval) return@clickable
        lastClickTime = currentTime
        onClick()
    }
}
inline fun Modifier.noRippleDebounceClickable(
    enabled: Boolean = true,
    debounceInterval: Long = 1000L,
    crossinline onClick: () -> Unit,
): Modifier = composed {
    var lastClickTime by rememberSaveable { mutableStateOf(0L) }
    clickable(
        enabled = enabled,
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        val currentTime = currentTimeMillis()
        if ((currentTime - lastClickTime) < debounceInterval) return@clickable
        lastClickTime = currentTime
        onClick()
    }
}

fun Dp.toDpSize() = DpSize(this, this)
