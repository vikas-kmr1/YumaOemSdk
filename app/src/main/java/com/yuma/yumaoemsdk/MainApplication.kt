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
            .setClientKey("DUMMY_CLIENT_KEY_123") // TODO: Replace with real key
            .setMapApiKey("AIzaSyBTmktjliqw55JQNiCqDrdyJNCZYcqeFVE")
            .setEnvironment(if(BuildConfig.DEBUG) Environment.DEV else Environment.PROD)
            .build()
            
        YumaSdk.init(this, sdkConfig)
    }
}
