package com.yuma.oemsdk

import android.content.Context
import android.util.Log
import com.yuma.oemsdk.network.SdkNetworkClient
import io.ktor.client.plugins.auth.providers.BearerTokens


enum class Environment {
    DEV, PREPROD, PROD
}

/**
 * Configuration for initializing the Yuma SDK.
 *
 * @property clientKey  The unique client key issued by Yuma for authentication.
 * @property mapApiKey  The Google Maps API key used internally by the SDK's map screens.
 * @property environment The target backend environment.
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

        fun setClientKey(clientKey: String) = apply { this.clientKey = clientKey }
        fun setMapApiKey(mapApiKey: String) = apply { this.mapApiKey = mapApiKey }
        fun setEnvironment(environment: Environment) = apply { this.environment = environment }

        fun build(): YumaSdkConfiguration {
            require(clientKey.isNotBlank()) { "Client Key must not be blank" }
            require(mapApiKey.isNotBlank()) { "Map API Key must not be blank" }
            return YumaSdkConfiguration(clientKey, mapApiKey, environment)
        }
    }
}


/**
 * Main entry point for the Yuma OEM SDK.
 * Call [init] once in your Application class before using any SDK features.
 */
object YumaSdk {
    private const val TAG = "YumaSdk"

    @Volatile
    private var isInitialized = false

    private lateinit var applicationContext: Context
    private var config: YumaSdkConfiguration? = null

    // Holds the active session token after silent auth
    @Volatile
    private var bearerTokens: BearerTokens? = null

    // Internal network client — created manually, no DI
    internal var networkClient: SdkNetworkClient? = null
        private set

    /**
     * Initializes the Yuma SDK. Should be called in your Application class.
     *
     * @param context   Application context.
     * @param sdkConfig Configuration built via [YumaSdkConfiguration.Builder].
     */
    @JvmStatic
    fun init(context: Context, sdkConfig: YumaSdkConfiguration) {
        if (isInitialized) {
            Log.w(TAG, "Yuma SDK is already initialized.")
            return
        }

        synchronized(this) {
            if (isInitialized) return

            applicationContext = context.applicationContext
            config = sdkConfig

            val baseUrl = when (sdkConfig.environment) {
                Environment.DEV     -> "dev-backend-oem.yumax.app"
                Environment.PREPROD -> "preprod-backend-oem.yumax.app"
                Environment.PROD    -> "backend-oem.yumax.app"
            }

            // Build the Ktor client manually (no DI / no OkHttp)
            networkClient = SdkNetworkClient(
                baseUrl = baseUrl,
                enableLogging = sdkConfig.environment != Environment.PROD,
                tokenProvider = { bearerTokens },
                onTokenRefreshFailed = {
                    Log.e(TAG, "Session expired — SDK needs re-initialization.")
                    resetKtorClient()
                }
            )

            isInitialized = true
            Log.d(TAG, "✅ Yuma SDK initialized | env=${sdkConfig.environment} | url=$baseUrl")

            // TODO: Exchange clientKey for a JWT session token via a background coroutine here.
            //       On success: bearerTokens = BearerTokens(accessToken, refreshToken)
        }
    }

    /** Saves the session tokens after successful silent authentication. */
    internal fun saveSessionTokens(accessToken: String, refreshToken: String) {
        bearerTokens = BearerTokens(accessToken, refreshToken)
        Log.d(TAG, "Session tokens saved.")
    }

    /** Resets the Ktor client and clears session tokens (call on logout). */
    fun resetKtorClient() {
        bearerTokens = null
        networkClient?.close()
        networkClient = null
        Log.d(TAG, "🔄 SDK network client reset.")
    }

    /** Returns true if the SDK has been initialized. */
    fun isInitialized(): Boolean = isInitialized

    /** Returns the application context held by the SDK. */
    fun getApplicationContext(): Context = applicationContext

    /**
     * Returns the current SDK configuration.
     * @throws IllegalStateException if [init] has not been called.
     */
    fun getConfig(): YumaSdkConfiguration {
        check(isInitialized) { "Yuma SDK is not initialized. Call YumaSdk.init() first." }
        return config!!
    }
}




