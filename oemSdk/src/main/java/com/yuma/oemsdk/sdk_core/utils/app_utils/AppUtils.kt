package com.yumaoem.core.utils.app_utils

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.location.LocationManagerCompat
import com.yumaoem.core.utils.context.PlatformContext
import com.yumaoem.core.utils.global_events.HideKeyboard
import com.yumaoem.core.utils.global_events.controller.EventController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

fun closeApp() {
    exitProcess(0)
}

fun isLocationEnabled(): Boolean {
    val context = PlatformContext.getApplicationContext() as Context
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

fun hideKeyboard() {
    val scope = CoroutineScope(context = Dispatchers.Main.immediate)
    scope.launch {
        EventController.sendEvent(HideKeyboard)
    }
}

//TODO
//suspend fun getFcmToken(): String {
//    return FirebaseMessaging.getInstance().token.await()
//}

fun isAndroid12OrLower(): Boolean {
    return Build.VERSION.SDK_INT <= Build.VERSION_CODES.S
}


fun getAppVersion(): String {
    return try {
        val appContext = PlatformContext.getApplicationContext() as Context
        val pInfo = appContext.packageManager.getPackageInfo(
            appContext.packageName,
            0
        )
        pInfo.versionName ?: "N/A"
    } catch (e: PackageManager.NameNotFoundException) {
        "N/A"
    }
}
