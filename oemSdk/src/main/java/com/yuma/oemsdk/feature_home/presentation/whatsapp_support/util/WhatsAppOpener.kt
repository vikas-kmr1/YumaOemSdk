package com.yumaoem.feature_home.presentation.whatsapp_support.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.yumaoem.core.utils.context.PlatformContext
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.corepreference.impl.util.YumaPrefUtilImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.net.URLEncoder

fun openWhatsAppWithMessage() {
    val scope = CoroutineScope(Dispatchers.IO)
    val androidContext = PlatformContext.getApplicationContext()
    //val preferencesApi: YumaPrefUtilApi = GlobalContext.get().get()

    scope.launch {
//        val supportDetails = preferencesApi.getSupportDetails().firstOrNull()
//        if (supportDetails == null){
//            SnackbarController.sendEvent(SnackbarEvent("Getting Support Details"))
//            return@launch
//        }
//
//        val phoneNumber = "+91${supportDetails.phoneNumber}"
//        val supportMessage = supportDetails.defaultMessage
        val url = getUrl("+919560220981","")//(phoneNumber, supportMessage))

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = url.toUri()
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            (androidContext as Context).startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            val appPackageName = "com.whatsapp"

            try {
                val marketIntent = Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=$appPackageName".toUri()
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                (androidContext as Context).startActivity(marketIntent)
            } catch (e: ActivityNotFoundException) {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                (androidContext as Context).startActivity(webIntent)
            }
        }
    }
}


private fun getUrl(
    phoneNumber: String,
    message: String
): String {
    return "https://wa.me/$phoneNumber?text=${URLEncoder.encode(message, "UTF-8")}"
}
