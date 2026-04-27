package com.yuma.oemsdk

import android.content.Context
import android.content.Intent
import android.util.Log
import com.yuma.oemsdk.YumaSdk.init
import com.yuma.oemsdk.data.network.SdkHomeRemoteDataSource
import com.yuma.oemsdk.location.SdkLocationManager
import com.yuma.oemsdk.network.SdkNetworkClient
import com.yuma.oemsdk.network.SdkSilentAuthManager
import com.yuma.oemsdk.prefs.SdkPrefManager
import io.ktor.client.plugins.auth.providers.BearerTokens


enum class Environment {
    DEV, PREPROD, PROD
}

/**
 * Configuration for initializing the Yuma OEM SDK.
 *
 * @property clientKey  The unique client key issued by Yuma — used for silent authentication.
 * @property mapApiKey  The Google Maps API key used internally by the SDK's map screens.
 * @property environment The target backend environment. Defaults to [Environment.PROD].
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
 *
 * ## Usage
 * ```kotlin
 * // In your Application class:
 * YumaSdk.init(
 *     context = this,
 *     sdkConfig = YumaSdkConfiguration.Builder()
 *         .setClientKey("YOUR_CLIENT_KEY")
 *         .setMapApiKey("YOUR_MAP_API_KEY")
 *         .setEnvironment(Environment.PROD)
 *         .build()
 * )
 *
 * // In your Activity / Fragment:
 * YumaSdk.launchHome(context)
 * ```
 */
object YumaSdk {
    private const val TAG = "YumaSdk"

    @Volatile
    private var isInitialized = false

    private lateinit var applicationContext: Context
    private var config: YumaSdkConfiguration? = null

    // In-memory auth token state — fed to Ktor bearer plugin
    @Volatile
    private var bearerTokens: BearerTokens? = null

    // ─── Initialization ───────────────────────────────────────────────────────

    /**
     * Initializes the SDK. Must be called in your Application class before any
     * other SDK method.
     *
     * @param context   Application context.
     * @param sdkConfig Configuration built via [YumaSdkConfiguration.Builder].
     */
    @JvmStatic
    fun init(context: Context, sdkConfig: YumaSdkConfiguration) {
        if (isInitialized) {
            Log.w(TAG, "YumaSdk is already initialized — skipping.")
            return
        }

        synchronized(this) {
            if (isInitialized) return

            applicationContext = context.applicationContext
            config = sdkConfig

            val baseUrl = when (sdkConfig.environment) {
                Environment.DEV -> "dev-backend-oem.yumax.app"
                Environment.PREPROD -> "preprod-backend-oem.yumax.app"
                Environment.PROD -> "backend-oem.yumax.app"
            }
            val enableLogging = sdkConfig.environment != Environment.PROD

            // 1. Preferences (DataStore — no 3rd party DI)
            val prefManager = SdkPrefManager(applicationContext)

            // 2. Network client (Ktor + native Android engine — no OkHttp)
            val networkClient = SdkNetworkClient(
                baseUrl = baseUrl,
                enableLogging = enableLogging,
                tokenProvider = { bearerTokens },
                onTokenRefreshFailed = {
                    Log.e(TAG, "Session expired — resetting SDK.")
                    resetKtorClient()
                }
            )

            // 3. Silent auth manager
            val silentAuthManager = SdkSilentAuthManager(
                networkClient = networkClient,
                onTokensObtained = { access, refresh ->
                    prefManager.saveTokens(access, refresh)
                    bearerTokens = BearerTokens(access, refresh)
                }
            )

            // 4. Location manager
            val locationManager = SdkLocationManager(applicationContext)

            // 5. Home data source (mirrors OEM HomeRemoteDataSource — no Koin)
            val remoteDataSource = SdkHomeRemoteDataSource(networkClient)

            // 6. Wire everything into the service locator
            SdkServiceLocator.initialize(
                networkClient = networkClient,
                silentAuthManager = silentAuthManager,
                prefManager = prefManager,
                locationManager = locationManager,
                remoteDataSource = remoteDataSource
            )

            // 7. Initialize Network Inspector (Debug-only logic abstracted)
            com.yuma.oemsdk.network.SdkNetworkInspector.create().initialize(applicationContext)

            isInitialized = true
            Log.d(TAG, "✅ YumaSdk initialized | env=${sdkConfig.environment} | url=$baseUrl")
        }
    }

    // ─── Launch ───────────────────────────────────────────────────────────────

    /**
     * Launches the SDK's full UI experience.
     *
     * This starts [SdkMainActivity] which automatically performs silent authentication
     * and navigates to the Home screen upon success — no login screen shown.
     *
     * @param context Any Android context (Activity, Application, etc.)
     * @throws IllegalStateException if [init] has not been called.
     */
    @JvmStatic
    fun launchHome(context: Context) {
        check(isInitialized) { "YumaSdk not initialized. Call YumaSdk.init() first." }
        val intent = Intent(context, SdkMainActivity::class.java).apply {
            // Ensure new task if launching from non-Activity context
            if (context !is android.app.Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(intent)
    }

    // ─── Session Management ───────────────────────────────────────────────────

    /** Called after successful silent auth to store the JWT tokens in memory. */
    internal fun saveSessionTokens(accessToken: String, refreshToken: String) {
        bearerTokens = BearerTokens(accessToken, refreshToken)
        Log.d(TAG, "Session tokens saved.")
    }

    /** Resets the Ktor client and clears all tokens (e.g. on logout or session expiry). */
    fun resetKtorClient() {
        bearerTokens = null
        SdkServiceLocator.reset()
        Log.d(TAG, "🔄 SDK network client reset.")
    }

    // ─── Accessors ────────────────────────────────────────────────────────────

    /** Returns true if [init] has been called. */
    fun isInitialized(): Boolean = isInitialized

    /** Returns the application context held by the SDK. */
    fun getApplicationContext(): Context = applicationContext

    /**
     * Returns the current SDK configuration.
     * @throws IllegalStateException if [init] has not been called.
     */
    fun getConfig(): YumaSdkConfiguration {
        check(isInitialized) { "YumaSdk is not initialized. Call YumaSdk.init() first." }
        return config!!
    }

}
