package com.yuma.oemsdk

import com.yuma.oemsdk.data.network.SdkHomeRemoteDataSource
import com.yuma.oemsdk.location.SdkLocationManager
import com.yuma.oemsdk.network.SdkNetworkClient
import com.yuma.oemsdk.network.SdkSilentAuthManager
import com.yuma.oemsdk.prefs.SdkPrefManager

/**
 * Internal dependency container for the Yuma OEM SDK.
 *
 * Replaces DI frameworks (Koin/Hilt) entirely — all dependencies are constructed
 * manually during [YumaSdk.init] and held here for the SDK's lifetime.
 *
 * **Internal use only.** Not exposed to the host application.
 */
internal object SdkServiceLocator {

    // ── Network ──────────────────────────────────────────────────────────────
    lateinit var networkClient: SdkNetworkClient
        private set

    lateinit var silentAuthManager: SdkSilentAuthManager
        private set

    // ── Preferences ──────────────────────────────────────────────────────────
    lateinit var prefManager: SdkPrefManager
        private set

    // ── Location ─────────────────────────────────────────────────────────────
    lateinit var locationManager: SdkLocationManager
        private set

    // ── Data Sources ──────────────────────────────────────────────────────────
    lateinit var remoteDataSource: SdkHomeRemoteDataSource
        private set

    // ── Flags ─────────────────────────────────────────────────────────────────
    private var initialized = false

    /**
     * Populates all dependencies. Called once by [YumaSdk.init].
     */
    fun initialize(
        networkClient: SdkNetworkClient,
        silentAuthManager: SdkSilentAuthManager,
        prefManager: SdkPrefManager,
        locationManager: SdkLocationManager,
        remoteDataSource: SdkHomeRemoteDataSource
    ) {
        check(!initialized) { "SdkServiceLocator is already initialized." }
        this.networkClient = networkClient
        this.silentAuthManager = silentAuthManager
        this.prefManager = prefManager
        this.locationManager = locationManager
        this.remoteDataSource = remoteDataSource
        initialized = true
    }

    /**
     * Resets all dependencies (called on SDK reset / logout).
     */
    fun reset() {
        if (initialized) {
            networkClient.close()
            initialized = false
        }
    }

    fun isInitialized() = initialized
}
