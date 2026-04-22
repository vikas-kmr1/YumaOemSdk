package com.yuma.yumaoemsdk

import android.app.Application
import com.yuma.oemsdk.Environment
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.YumaSdkConfiguration

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Yuma OEM SDK
        val sdkConfig = YumaSdkConfiguration.Builder()
            .setClientKey("DUMMY_CLIENT_KEY_123") // TODO: Replace with real key
            .setMapApiKey("AIzaSyDgUJfdi2Ba9bh5FrzTIofMDCIFDcK02kM")
            .setEnvironment(Environment.DEV)
            .build()
            
        YumaSdk.init(this, sdkConfig)
    }
}
