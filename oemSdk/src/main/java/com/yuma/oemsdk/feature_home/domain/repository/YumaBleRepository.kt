package com.yumaoem.feature_home.domain.repository

import com.yumacustomer.new_ble_sdk.data.CommonSessionConfig
import com.yumacustomer.new_ble_sdk.data.SmartSwapSubmitResponse
import com.yumacustomer.new_ble_sdk.data.SwapStatusResultDto
import com.yumacustomer.new_ble_sdk.data.YumaResponse
import kotlinx.coroutines.flow.Flow

interface YumaBleRepository {
    suspend fun initializeSession(sessionConfig: CommonSessionConfig, enableAnalytics: Boolean = true)
    suspend fun startSwap(qrCode: String)
    suspend fun swapStatus(tokenId: Long) : SwapStatusResultDto
    suspend fun submitSwapResult()
    suspend fun submitBatteryQr(batteryQr: List<String>)
    suspend fun cleanupSession()
    suspend fun triggerAccessType()
    fun observeResponses(): Flow<YumaResponse>
    fun isSessionActive(): Boolean
    suspend fun smartSwapSubmit() : SmartSwapSubmitResponse
}