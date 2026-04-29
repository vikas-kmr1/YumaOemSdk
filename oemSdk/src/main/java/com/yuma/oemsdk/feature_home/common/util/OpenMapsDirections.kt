package com.yumaoem.feature_home.common.util

import android.content.Intent
import androidx.core.net.toUri
import com.yumaoem.core.utils.context.AndroidContextProvider

fun openMapsDirections(latitude: Double, longitude: Double) {
    val context = AndroidContextProvider.context // Use your own context provider
    val uri = "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    if (intent.resolveActivity(context!!.packageManager) != null) {
        context.startActivity(intent)
    } else {
        android.widget.Toast.makeText(context, "Google Maps not installed", android.widget.Toast.LENGTH_SHORT).show()
    }
}
