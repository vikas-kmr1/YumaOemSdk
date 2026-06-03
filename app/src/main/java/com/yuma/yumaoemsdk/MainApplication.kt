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
            .setClientId(31)
            .setClientSecret("X9mBp6MsY6W")
            .setAuthCode("oMOXxlcCwoEjs0MwWeS5xA")
            .setMapApiKey("AIzaSyBTmktjliqw55JQNiCqDrdyJNCZYcqeFVE")
            .setEnvironment(if (BuildConfig.DEBUG) Environment.PREPROD else Environment.PROD)
            .build()

        YumaSdk.init(this, sdkConfig)
    }
}
