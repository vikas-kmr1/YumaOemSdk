package com.yuma.oemsdk

import android.app.Application
import android.content.Context
import android.util.Log
import com.yuma.oemsdk.YumaSdk.init


enum class Environment {
    DEV, PREPROD, PROD
}

/**
 * Configuration for initializing the Yuma SDK.
 *
 * @property clientKey The unique identifier for the client.
 * @property mapApiKey The map identifier associated with the client.
 */

class YumaSdkConfiguration private constructor(
    val clientKey: String,
    val mapApiKey: String,
    val environment: Environment
) {
    class Builder {
        private var clientKey: String = ""
        private var mapApiKey: String = ""
        private var environment: Environment = Environment.PROD

        fun setClientId(clientkey: String) = apply { this.clientKey = clientKey }
        fun setApiKey(apiKey: String) = apply { this.mapApiKey = apiKey }
        fun setEnvironment(environment: Environment) = apply { this.environment = environment }

        fun build(): YumaSdkConfiguration {
            require(clientKey.isNotBlank()) { "Client ID must not be blank" }
            require(mapApiKey.isNotBlank()) { "API Key must not be blank" }
            return YumaSdkConfiguration(clientKey, mapApiKey, environment)
        }
    }
}


/**
 * Main entry point for the Yuma OEM SDK.
 * Use [init] to initialize the SDK before using any of its features.
 */
object YumaSdk {
    private const val TAG = "YumaSdk"

    @Volatile
    private var isInitialized = false

    private lateinit var applicationContext: Context

    private var config: YumaSdkConfiguration? = null

    /**
     * Initializes the Yuma SDK. This should ideally be called in your Application class.
     *
     * @param context Application context.
     * @param sdkConfig The configuration parameters for the SDK.
     */
    @JvmStatic
    fun init(context: Context, sdkConfig: YumaSdkConfiguration) {
        if (isInitialized) {
            Log.w(TAG, "Yuma SDK is already initialized.")
            return
        }

        synchronized(this) {
            if (isInitialized) return

            // Store application context to prevent memory leaks
            applicationContext = context.applicationContext

            config = sdkConfig
            isInitialized = true

            Log.d(TAG, "Yuma SDK initialized successfully with clientId: ${sdkConfig.clientKey}")
            // TODO: Add any other necessary initialization logic here
        }
    }

    /**
     * Returns true if the SDK has been initialized.
     */
    fun isInitialized(): Boolean = isInitialized

    fun getApplicationContext(): Context = this.applicationContext

    /**
     * Retrieves the current configuration.
     * 
     * @throws IllegalStateException if the SDK hasn't been initialized yet.
     */
    fun getConfig(): YumaSdkConfiguration {
        check(isInitialized) { "Yuma SDK is not initialized. Call YumaSdk.init() first." }
        return config!!
    }
}
