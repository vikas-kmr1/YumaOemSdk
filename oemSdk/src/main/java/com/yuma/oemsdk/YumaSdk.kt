package com.yuma.oemsdk

import android.content.Context
import android.content.Intent
import android.util.Log
import com.yuma.oemsdk.YumaSdk.init
import com.yuma.oemsdk.core_network.HttpClientApiImpl
import com.yuma.oemsdk.onboarding.SilentAuthViewModel
import com.yumacustomer.core_logger.api.LoggerApi
import com.yumacustomer.core_logger.impl.LoggerApiImpl
import com.yumaoem.core.app_navigation_state.NavigationStateRepository
import com.yumaoem.core.utils.context.AndroidContextProvider
import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core.utils.device_info.DeviceInfoProvider
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.corepreference.createDataStore
import com.yumaoem.corepreference.impl.PreferenceApiImpl
import com.yumaoem.corepreference.impl.util.YumaPrefUtilImpl
import com.yumaoem.feature_home.common.notification.ServiceLauncher
import com.yumaoem.feature_home.data.network.HomeRemoteDataSource
import com.yumaoem.feature_home.data.network.YuzenRemoteDataSource
import com.yumaoem.feature_home.data.repository.HomeRepositoryImpl
import com.yumaoem.feature_home.domain.usecase.get_battery_details.GetBatteryDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.maps.all_station_markers.GetAllStationsUseCase
import com.yumaoem.feature_home.domain.usecase.maps.route_info.GetRouteInfoUseCase
import com.yumaoem.feature_home.domain.usecase.maps.station_operation_status.GetStationOperationStatusUseCase
import com.yumaoem.feature_home.domain.usecase.token_booking.book_token.BookTokenUseCase
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.viewmodel.HomeViewModel
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.user_current_location_provider.LocationProvider
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.MapViewModel
import com.yumaoem.feature_onboarding.data.network.OnboardingRemoteDataSource
import com.yumaoem.feature_onboarding.data.repository.OnboardingRepositoryImpl
import com.yumaoem.feature_onboarding.domain.use_case.drop_off.GetDropOffDataUseCase
import com.yumaoem.feature_onboarding.domain.use_case.verify_otp.SilentAuthUseCase
import kotlinx.serialization.json.Json


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
class YumaSdkConfiguration constructor(
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


    // Factory for SilentAuthViewModel
    internal var silentAuthViewModelFactory: SilentAuthViewModel.Factory? = null

    internal lateinit var homeViewModelFactory: HomeViewModel.Factory

    internal lateinit var mapViewModel: MapViewModel.Factory

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
            AndroidContextProvider.context = applicationContext

            config = sdkConfig


            val enableLogging = sdkConfig.environment != Environment.PROD


            // 1. Preferences (DataStore — no 3rd party DI)
            val prefManager = initYumaPrefManager(applicationContext)

            // 2. Logger (no 3rd party DI)
            val loggerApi = initYumaLogger(enableLogging)

            // 3. Network client (Ktor + native Android engine — no OkHttp)
            val networkClient = initYumaNetworkClient(
                shouldEnableLogging = enableLogging,
                loggerApi = loggerApi,
                preferenceUtilApi = prefManager,
                environment = sdkConfig.environment
            )


            // 3. Location provider
            val locationProvider = initYumaLocationProvider(applicationContext)

            // 4. Core Location Provider
            val coreLocationProvider = initYumaCoreLocationProvider(applicationContext)

            // 5. SilentAuthManager

            val deviceInfoProvider = DeviceInfoProvider(applicationContext)

            val onboardingDatasource =
                OnboardingRemoteDataSource(networkClient, coreLocationProvider)
            val yuzenDataSource = YuzenRemoteDataSource(
                networkClient,
                Json { ignoreUnknownKeys = true },
                coreLocationProvider
            )
            val homeDataSource = HomeRemoteDataSource(
                networkClient,
                Json { ignoreUnknownKeys = true },
                coreLocationProvider
            )

            val navigationStateRepository = NavigationStateRepository()
            val homeRepository = HomeRepositoryImpl(
                homeDataSource,
                yuzenDataSource
            )



            silentAuthViewModelFactory = SilentAuthViewModel.Factory(
                navigationStateRepository = navigationStateRepository,
                preferenceApi = prefManager,
                dataSource = onboardingDatasource,
                silentAuthUseCase = SilentAuthUseCase(OnboardingRepositoryImpl(onboardingDatasource)),
                dropOffDataUseCase = GetDropOffDataUseCase(homeRepository),
                deviceInfoProvider = deviceInfoProvider
            )

            // 6. HomeViewModel
            homeViewModelFactory = HomeViewModel.Factory(
                locationProvider = locationProvider,
                yumaPrefUtil = prefManager,
                supportDetailsUseCase = com.yumaoem.feature_home.domain.usecase.support_details.GetWhatsappSupprtDetailsUseCase(
                    HomeRepositoryImpl(homeDataSource, yuzenDataSource)
                ),
                navigationStateRepository = navigationStateRepository,
                serviceLauncher = com.yumaoem.feature_home.common.notification.ServiceLauncher(
                    applicationContext
                ),
            )

            // 7. MapViewModel
            val bookTokenUseCase = BookTokenUseCase(homeRepository)
            val getAllStationsUseCase: GetAllStationsUseCase = GetAllStationsUseCase(homeRepository)
            val getRouteInfoUseCase: GetRouteInfoUseCase = GetRouteInfoUseCase(homeRepository)
            val stationOperationStatusUseCase = GetStationOperationStatusUseCase(homeRepository)
            val getBatteryDetailsUseCase: GetBatteryDetailsUseCase =
                GetBatteryDetailsUseCase(homeRepository)

            mapViewModel = MapViewModel.Factory(
                locationProvider = locationProvider,
                bookTokenUseCase = bookTokenUseCase,
                getAllStationsUseCase = getAllStationsUseCase,
                getRouteInfoUseCase = getRouteInfoUseCase,
                stationOperationStatusUseCase = stationOperationStatusUseCase,
                prefUtilApi = prefManager,
                serviceLauncher = ServiceLauncher(context),
                getBatteryDetailsUseCase = getBatteryDetailsUseCase
            )

            isInitialized = true
            Log.d(TAG, "✅ YumaSdk initialized | env=${sdkConfig.environment}")
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


    /** Resets the Ktor client and clears all tokens (e.g. on logout or session expiry). */
    fun resetKtorClient() {
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


    /* ———————————————————————————————————init-core-services——————————————————————————————————————————————*/

    private fun initYumaNetworkClient(
        shouldEnableLogging: Boolean,
        loggerApi: LoggerApi,
        preferenceUtilApi: YumaPrefUtilApi,
        environment: Environment = Environment.PROD,
    ): HttpClientApiImpl {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = false
            isLenient = true
            useAlternativeNames = true
            encodeDefaults = true
            explicitNulls = false
        }
        return HttpClientApiImpl(
            shouldEnableLogging = shouldEnableLogging,
            loggerApi = loggerApi,
            preferenceUtilApi = preferenceUtilApi,
            json = json,
            environment = environment
        )
    }

    private fun initYumaLogger(shouldEnableLogging: Boolean): LoggerApi =
        LoggerApiImpl(shouldEnableLogging)

    private fun initYumaPrefManager(context: Context): YumaPrefUtilApi {
        val prefrenceApi = PreferenceApiImpl(
            createDataStore(context = context)
        )
        return YumaPrefUtilImpl(prefrenceApi)
    }

    private fun initYumaLocationProvider(context: Context): LocationProvider =
        LocationProvider(context)

    private fun initYumaCoreLocationProvider(context: Context): CoreLocationProvider =
        CoreLocationProvider(context)

//    private fun initYumaJitsuAnlatyticApi(): AnalyticsApi {
//        val segmentAnalytics = SegmentAnalytics()
//        return AnalyticsManager(
//
//        )
//    }
}
