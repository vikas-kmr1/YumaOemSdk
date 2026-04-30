package com.yuma.oemsdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yuma.oemsdk.YumaSdk.init
import com.yuma.oemsdk.core_network.HttpClientApiImpl
import com.yuma.oemsdk.onboarding.SilentAuthViewModel
import com.yumacustomer.core_logger.api.LoggerApi
import com.yumacustomer.core_logger.impl.LoggerApiImpl
import com.yumacustomer.new_ble_sdk.api.YumaBleSDK
import com.yumaoem.core.app_navigation_state.NavigationStateRepository
import com.yumaoem.core.utils.context.AndroidContextProvider
import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core.utils.device_info.DeviceInfoProvider
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.corepreference.createDataStore
import com.yumaoem.corepreference.impl.PreferenceApiImpl
import com.yumaoem.corepreference.impl.util.YumaPrefUtilImpl
import com.yumaoem.feature_home.common.customer_support.CustomerSupportCallInteractor
import com.yumaoem.feature_home.common.notification.ServiceLauncher
import com.yumaoem.feature_home.common.util.analytics_utils.CommonAnalyticsParamsProvider
import com.yumaoem.feature_home.data.network.HomeRemoteDataSource
import com.yumaoem.feature_home.data.network.YuzenRemoteDataSource
import com.yumaoem.feature_home.data.repository.HomeRepositoryImpl
import com.yumaoem.feature_home.data.repository.PaymentRepositoryImpl
import com.yumaoem.feature_home.data.repository.YumaBleRepositoryImpl
import com.yumaoem.feature_home.domain.repository.PaymentRepository
import com.yumaoem.feature_home.domain.usecase.auto_dialer.AutoDialerRequestUseCase
import com.yumaoem.feature_home.domain.usecase.ble.CleanupBleSessionUseCase
import com.yumaoem.feature_home.domain.usecase.ble.InitializeBleSessionUseCase
import com.yumaoem.feature_home.domain.usecase.ble.ObserveBleResponsesUseCase
import com.yumaoem.feature_home.domain.usecase.ble.SmartSwapSubmitUseCase
import com.yumaoem.feature_home.domain.usecase.ble.StartSwapUseCase
import com.yumaoem.feature_home.domain.usecase.ble.SubmitChargedBatteryQrUseCase
import com.yumaoem.feature_home.domain.usecase.ble.SubmitSwapResultUseCase
import com.yumaoem.feature_home.domain.usecase.ble.SwapStatusUseCase
import com.yumaoem.feature_home.domain.usecase.get_battery_details.GetBatteryDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.logout_user.LogoutUserUseCase
import com.yumaoem.feature_home.domain.usecase.maps.all_station_markers.GetAllStationsUseCase
import com.yumaoem.feature_home.domain.usecase.maps.route_info.GetRouteInfoUseCase
import com.yumaoem.feature_home.domain.usecase.maps.station_operation_status.GetStationOperationStatusUseCase
import com.yumaoem.feature_home.domain.usecase.payments.payment_home.GetPaymentPlansUseCase
import com.yumaoem.feature_home.domain.usecase.profile_screen.GetSwapHistoryUseCase
import com.yumaoem.feature_home.domain.usecase.profile_screen.GetUserDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.support_details.GetWhatsappSupprtDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.token_booking.book_token.BookTokenUseCase
import com.yumaoem.feature_home.domain.usecase.token_status.GetTokenStatusUseCase
import com.yumaoem.feature_home.presentation.diy_flow.CommonSessionConfigFactory
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapInProgressViewModel
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.viewmodel.HomeViewModel
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.user_current_location_provider.LocationProvider
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.MapViewModel
import com.yumaoem.feature_home.presentation.payments.payment_home.PaymentHomeViewModel
import com.yumaoem.feature_home.presentation.profile_screen.ProfileViewModel
import com.yumaoem.feature_onboarding.data.network.OnboardingRemoteDataSource
import com.yumaoem.feature_onboarding.data.repository.OnboardingRepositoryImpl
import com.yumaoem.feature_onboarding.domain.use_case.drop_off.GetDropOffDataUseCase
import com.yumaoem.feature_onboarding.domain.use_case.verify_otp.SilentAuthUseCase
import kotlinx.serialization.json.Json

internal const val emptyString = ""

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

class YumaSdkConfiguration(
    val clientKey: String,
    val mapApiKey: String,
    val environment: Environment
) {
    class Builder {
        private var clientKey: String = emptyString
        private var mapApiKey: String = emptyString
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
 * Singleton @YumaSdk class.
 * Main entry point for the Yuma OEM SDK.
 **/
object YumaSdk {
    private const val TAG = "YumaSdk"

    @Volatile
    private var isInitialized = false

    private lateinit var applicationContext: Context
    private lateinit var config: YumaSdkConfiguration

    // View Model Factories
    internal lateinit var silentAuthViewModelFactory: SilentAuthViewModel.Factory
    internal lateinit var homeViewModelFactory: HomeViewModel.Factory
    internal lateinit var mapViewModelFactory: MapViewModel.Factory
    internal lateinit var profileViewModelFactory: ProfileViewModel.Factory
    internal lateinit var diySwapInProgressViewModelFactory: DiySwapInProgressViewModel.Factory
    internal lateinit var paymentHomeViewModelFactory: PaymentHomeViewModel.Factory

    // Core Services
    internal lateinit var prefManager: YumaPrefUtilApi
    private val jsonConfig = Json { ignoreUnknownKeys = true }

    // ─── Initialization ───────────────────────────────────────────────────────
    /**
     * Initializes the SDK. Must be called in the Client's Application class before any
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
            val enableLogging = sdkConfig.environment != Environment.PROD

            applicationContext = context.applicationContext
            AndroidContextProvider.context = applicationContext
            config = sdkConfig

            // 1. Core Services Setup
            prefManager = initYumaPrefManager(applicationContext)
            val loggerApi = LoggerApiImpl(enableLogging)
            val networkClient =
                initYumaNetworkClient(enableLogging, loggerApi, prefManager, sdkConfig.environment)
            val locationProvider = LocationProvider(applicationContext)
            val coreLocationProvider = CoreLocationProvider(applicationContext)
            val deviceInfoProvider = DeviceInfoProvider(applicationContext)
            val serviceLauncher = ServiceLauncher(applicationContext)
            val navigationStateRepository = NavigationStateRepository()

            // 2. Data Sources & Repositories Setup
            val onboardingDatasource =
                OnboardingRemoteDataSource(networkClient, coreLocationProvider)
            val yuzenDataSource =
                YuzenRemoteDataSource(networkClient, jsonConfig, coreLocationProvider)
            val homeDataSource =
                HomeRemoteDataSource(networkClient, jsonConfig, coreLocationProvider)
            val homeRepository = HomeRepositoryImpl(homeDataSource, yuzenDataSource)

            // 3. Shared Use Cases
            val getBatteryDetailsUseCase = GetBatteryDetailsUseCase(homeRepository)
            val getUserDetailsUseCase = GetUserDetailsUseCase(prefManager)
            val autoDialerRequestUseCase = AutoDialerRequestUseCase(homeRepository)
            val commonAnalyticsParamsProvider = CommonAnalyticsParamsProvider(prefManager)

            // 4. Initialize ViewModel Factories
            silentAuthViewModelFactory = buildSilentAuthViewModelFactory(
                navigationStateRepository,
                onboardingDatasource,
                homeRepository,
                deviceInfoProvider,
                coreLocationProvider
            )

            homeViewModelFactory = buildHomeViewModelFactory(
                locationProvider, homeRepository, navigationStateRepository, serviceLauncher
            )

            mapViewModelFactory = buildMapViewModelFactory(
                locationProvider, homeRepository, serviceLauncher, getBatteryDetailsUseCase
            )

            profileViewModelFactory = buildProfileViewModelFactory(
                getUserDetailsUseCase,
                getBatteryDetailsUseCase,
                homeRepository,
                homeDataSource,
                loggerApi,
                commonAnalyticsParamsProvider
            )

            diySwapInProgressViewModelFactory = buildDiySwapViewModelFactory(
                applicationContext,
                sdkConfig.environment,
                homeRepository,
                coreLocationProvider,
                getUserDetailsUseCase,
                getBatteryDetailsUseCase,
                autoDialerRequestUseCase,
                commonAnalyticsParamsProvider,
                loggerApi
            )

            paymentHomeViewModelFactory = buildPaymentHomeViewModel(homeDataSource, prefManager, loggerApi)

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
    fun launchSdk(context: Context) {
        check(isInitialized) { "YumaSdk not initialized. Call YumaSdk.init() first." }
        val intent = Intent(context, SdkMainActivity::class.java).apply {
            // Ensure new task if launching from non-Activity context
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(intent)
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
        return config
    }

    // ─── Factory Builders ─────────────────────────────────────────────────────
    private fun buildSilentAuthViewModelFactory(
        navigationStateRepository: NavigationStateRepository,
        onboardingDatasource: OnboardingRemoteDataSource,
        homeRepository: HomeRepositoryImpl,
        deviceInfoProvider: DeviceInfoProvider,
        coreLocationProvider: CoreLocationProvider
    ): SilentAuthViewModel.Factory {
        return SilentAuthViewModel.Factory(
            navigationStateRepository = navigationStateRepository,
            preferenceApi = prefManager,
            dataSource = onboardingDatasource,
            silentAuthUseCase = SilentAuthUseCase(OnboardingRepositoryImpl(onboardingDatasource)),
            dropOffDataUseCase = GetDropOffDataUseCase(homeRepository),
            deviceInfoProvider = deviceInfoProvider,
            locationProvider = coreLocationProvider
        )
    }

    private fun buildHomeViewModelFactory(
        locationProvider: LocationProvider,
        homeRepository: HomeRepositoryImpl,
        navigationStateRepository: NavigationStateRepository,
        serviceLauncher: ServiceLauncher
    ): HomeViewModel.Factory {
        return HomeViewModel.Factory(
            locationProvider = locationProvider,
            yumaPrefUtil = prefManager,
            supportDetailsUseCase = GetWhatsappSupprtDetailsUseCase(homeRepository),
            navigationStateRepository = navigationStateRepository,
            serviceLauncher = serviceLauncher
        )
    }

    private fun buildMapViewModelFactory(
        locationProvider: LocationProvider,
        homeRepository: HomeRepositoryImpl,
        serviceLauncher: ServiceLauncher,
        getBatteryDetailsUseCase: GetBatteryDetailsUseCase
    ): MapViewModel.Factory {
        return MapViewModel.Factory(
            locationProvider = locationProvider,
            bookTokenUseCase = BookTokenUseCase(homeRepository),
            getAllStationsUseCase = GetAllStationsUseCase(homeRepository),
            getRouteInfoUseCase = GetRouteInfoUseCase(homeRepository),
            stationOperationStatusUseCase = GetStationOperationStatusUseCase(homeRepository),
            prefUtilApi = prefManager,
            serviceLauncher = serviceLauncher,
            getBatteryDetailsUseCase = getBatteryDetailsUseCase
        )
    }

    private fun buildProfileViewModelFactory(
        getUserDetailsUseCase: GetUserDetailsUseCase,
        getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
        homeRepository: HomeRepositoryImpl,
        homeDataSource: HomeRemoteDataSource,
        loggerApi: LoggerApi,
        analyticsParamsProvider: CommonAnalyticsParamsProvider
    ): ProfileViewModel.Factory {
        return ProfileViewModel.Factory(
            getUserDetailsUseCase = getUserDetailsUseCase,
            getSwapHistoryUseCase = GetSwapHistoryUseCase(homeRepository),
            getBatteryDetailsUseCase = getBatteryDetailsUseCase,
            logoutUserUseCase = LogoutUserUseCase(homeRepository),
            dataSource = homeDataSource,
            loggerApi = loggerApi,
            prefUtilApi = prefManager,
            commonAnalyticsParamsProvider = analyticsParamsProvider
        )
    }

    private fun buildDiySwapViewModelFactory(
        context: Context,
        environment: Environment,
        homeRepository: HomeRepositoryImpl,
        coreLocationProvider: CoreLocationProvider,
        getUserDetailsUseCase: GetUserDetailsUseCase,
        getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
        autoDialerRequestUseCase: AutoDialerRequestUseCase,
        analyticsParamsProvider: CommonAnalyticsParamsProvider,
        loggerApi: LoggerApi
    ): DiySwapInProgressViewModel.Factory {
        val yumaBleSDK = YumaBleSDK(context, environment.name)
        val yumaBleRepository = YumaBleRepositoryImpl(yumaBleSDK)
        return DiySwapInProgressViewModel.Factory(
            initializeBleSessionUseCase = InitializeBleSessionUseCase(yumaBleRepository),
            startSwapUseCase = StartSwapUseCase(yumaBleRepository),
            swapStatusUseCase = SwapStatusUseCase(yumaBleRepository),
            submitSwapResultUseCase = SubmitSwapResultUseCase(yumaBleRepository),
            cleanupBleSessionUseCase = CleanupBleSessionUseCase(yumaBleRepository),
            observeResponses = ObserveBleResponsesUseCase(yumaBleRepository),
            getTokenStatusUseCase = GetTokenStatusUseCase(homeRepository),
            autoDialerRequestUseCase = autoDialerRequestUseCase,
            submitChargedBatteryQrUseCase = SubmitChargedBatteryQrUseCase(yumaBleRepository),
            smartSwapSubmitUseCase = SmartSwapSubmitUseCase(yumaBleRepository),
            getUserDetailsUseCase = getUserDetailsUseCase,
            getBatteryDetailsUseCase = getBatteryDetailsUseCase,
            commonAnalyticsParamsProvider = analyticsParamsProvider,
            loggerApi = loggerApi,
            prefsApi = prefManager,
            customerSupportCallInteractor = CustomerSupportCallInteractor(
                prefManager,
                autoDialerRequestUseCase
            ),
            commonSessionConfigFactory = CommonSessionConfigFactory(
                prefManager,
                coreLocationProvider
            ),
            json = jsonConfig
        )
    }

    private fun buildPaymentHomeViewModel(
        homeDataSource: HomeRemoteDataSource,
        preferenceApi: YumaPrefUtilApi,
        loggerApi: LoggerApi
    ): PaymentHomeViewModel.Factory {
        val paymentRepository: PaymentRepository = PaymentRepositoryImpl(homeDataSource)
        val getPaymentPlansUseCase: GetPaymentPlansUseCase =
            GetPaymentPlansUseCase(paymentRepository)
        return PaymentHomeViewModel.Factory(
            getPaymentPlansUseCase = getPaymentPlansUseCase,
            preferenceApi = preferenceApi,
            loggerApi = loggerApi
        )
    }


    // ─── Core Services Initializers ───────────────────────────────────────────
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

    private fun initYumaPrefManager(context: Context): YumaPrefUtilApi {
        return YumaPrefUtilImpl(PreferenceApiImpl(createDataStore(context)))
    }

//    private fun initYumaJitsuAnlatyticApi(): AnalyticsApi {
//        val segmentAnalytics = SegmentAnalytics()
//        return AnalyticsManager(
//
//        )
//    }

}

