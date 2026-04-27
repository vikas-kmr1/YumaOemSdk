package com.yumaoem.core.utils.vibration

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import com.yumaoem.core.utils.context.AndroidContextProvider

/**
 * Trigger a short vibration / haptic pulse on the device.
 *
 * @param durationMillis Desired pulse length in milliseconds.
 *        On iOS the system ignores the length and always produces
 *        the predefined vibration (~400 ms); on Android the full
 *        duration is respected on API 26+ and best-effort below.
 */
@RequiresPermission(Manifest.permission.VIBRATE)
fun vibrate(durationMillis: Long) {
    val appContext = AndroidContextProvider.context

    if (appContext == null) return

    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                durationMillis,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(durationMillis)
    }
}