package com.yuma.yumaoemsdk

import android.app.Application
import com.yuma.oemsdk.BuildConfig
import com.yuma.oemsdk.Environment
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.YumaSdkConfiguration

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Yuma OEM SDK
        val sdkConfig = YumaSdkConfiguration.Builder()
            .setClientId(5)
            .setClientSecret("qwerty")
            .setAuthCode("Y-PySAKbXK_lTURZOi0jpw")
            .setMapApiKey("AIzaSyBTmktjliqw55JQNiCqDrdyJNCZYcqeFVE")
            .setEnvironment(if (BuildConfig.DEBUG) Environment.DEV else Environment.PROD)
            .build()

        YumaSdk.init(this, sdkConfig)
    }
}
