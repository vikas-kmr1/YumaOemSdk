package com.yumaoem.feature_home.data.repository

import com.yumacustomer.new_ble_sdk.api.YumaBleSDK
import com.yumacustomer.new_ble_sdk.data.CommonSessionConfig
import com.yumacustomer.new_ble_sdk.data.SmartSwapSubmitResponse
import com.yumacustomer.new_ble_sdk.data.SwapStatusResultDto
import com.yumacustomer.new_ble_sdk.data.YumaResponse
import com.yumaoem.feature_home.domain.repository.YumaBleRepository
import kotlinx.coroutines.flow.Flow

class YumaBleRepositoryImpl(
    private val sdk: YumaBleSDK
) : YumaBleRepository {

    override suspend fun initializeSession(sessionConfig: CommonSessionConfig, enableAnalytics: Boolean) {
        sdk.initialize(sessionConfig, enableAnalytics)
    }

    override suspend fun startSwap(qrCode: String) {
        sdk.startSwap(qrCode)
    }

    override suspend fun swapStatus(tokenId: Long) : SwapStatusResultDto {
        return sdk.swapStatus(tokenId)
    }

    override suspend fun submitSwapResult() {
        sdk.submit()
    }

    override suspend fun submitBatteryQr(batteryQr: List<String>) {
        sdk.submit(batteryQr = batteryQr)
    }

    override suspend fun cleanupSession() {
        sdk.clean()
    }

    override suspend fun triggerAccessType() {
        sdk.triggerAccessType()
    }

    override fun observeResponses(): Flow<YumaResponse> {
        return sdk.getResponseFlow()
    }

    override fun isSessionActive(): Boolean {
        return sdk.isInitialized()
    }

    override suspend fun smartSwapSubmit() : SmartSwapSubmitResponse {
        return sdk.smartSwapSubmit()
    }
}