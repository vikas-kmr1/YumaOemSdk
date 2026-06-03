package com.yumaoem.feature_home.domain.usecase.ble

import com.yumacustomer.new_ble_sdk.data.CommonSessionConfig
import com.yumacustomer.new_ble_sdk.data.SmartSwapSubmitResponse
import com.yumacustomer.new_ble_sdk.data.SwapStatusResultDto
import com.yumacustomer.new_ble_sdk.data.YumaResponse
import com.yumaoem.feature_home.domain.repository.YumaBleRepository
import kotlinx.coroutines.flow.Flow

class InitializeBleSessionUseCase(
    private val repository: YumaBleRepository
) {
    suspend operator fun invoke(sessionConfig: CommonSessionConfig, enableAnalytics: Boolean = true) {
        repository.initializeSession(sessionConfig, enableAnalytics)
    }
}

class StartSwapUseCase(
    private val repository: YumaBleRepository
) {
    suspend operator fun invoke(qrCode: String) {
        repository.startSwap(qrCode)
    }
}

class SwapStatusUseCase(
    private val repository: YumaBleRepository
) {
    suspend operator fun invoke(tokenId: Long) : SwapStatusResultDto {
        return repository.swapStatus(tokenId)
    }
}

class SubmitSwapResultUseCase(
    private val repository: YumaBleRepository
) {
    suspend operator fun invoke() {
        repository.submitSwapResult()
    }
}

class SubmitChargedBatteryQrUseCase(
    private val repository: YumaBleRepository
) {
    suspend operator fun invoke(batteryQr: List<String>) {
        repository.submitBatteryQr(batteryQr = batteryQr)
    }
}


class CleanupBleSessionUseCase(
    private val repository: YumaBleRepository
) {
    suspend operator fun invoke() {
        repository.cleanupSession()
    }
}

class TriggerAccessTypeUseCase(
    private val repository: YumaBleRepository
) {
    suspend operator fun invoke() {
        repository.triggerAccessType()
    }
}

class ObserveBleResponsesUseCase(
    private val repository: YumaBleRepository
) {
    operator fun invoke(): Flow<YumaResponse> {
        return repository.observeResponses()
    }
}

class SmartSwapSubmitUseCase(
    private val repository: YumaBleRepository
) {
    suspend operator fun invoke() : SmartSwapSubmitResponse {
        return repository.smartSwapSubmit()
    }
}