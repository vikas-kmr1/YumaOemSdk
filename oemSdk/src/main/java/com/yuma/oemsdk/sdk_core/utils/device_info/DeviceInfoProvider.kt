package com.yumaoem.core.utils.device_info

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import com.yumaoem.core.model.device_info.DeviceInfo
import com.yumaoem.core.utils.context.PlatformContext
import java.util.Locale
import java.util.TimeZone

/**
 * A provider for retrieving device-specific information.
 *
 * This is an `expect` class, requiring a platform-specific `actual` implementation
 * to provide the concrete details of how device information is obtained.
 */

class DeviceInfoProvider(private val context: Context) {
    fun getDeviceInfo(): DeviceInfo {
        val config = context.resources.configuration
        val displayMetrics = context.resources.displayMetrics

        val screenResolution = "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}"
        val deviceType = if ((config.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            "tablet"
        } else {
            "phone"
        }

        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        return DeviceInfo(
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            osName = "Android",
            osVersion = Build.VERSION.RELEASE,
            appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown",
            locale = Locale.getDefault().toLanguageTag(),
            timezone = TimeZone.getDefault().id,
            screenResolution = screenResolution,
            deviceType = deviceType,
            androidId = androidId
        )
    }
}

// File: AppInfoUtil.android.kt (Android source set)

fun getAppVersion(): String {
    val context = PlatformContext.getApplicationContext()  as Context
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "unknown"
    } catch (e: Exception) {
        "unknown"
    }
}
