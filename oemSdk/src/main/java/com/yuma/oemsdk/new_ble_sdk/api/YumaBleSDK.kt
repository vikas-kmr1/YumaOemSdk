package com.yumacustomer.new_ble_sdk.api

import android.content.Context
import android.util.Log
import com.yuma.ble_sdk.YumaSDK
import com.yuma.ble_sdk.component.OEMSDKComponent
import com.yuma.ble_sdk.config.SessionConfig
import com.yuma.ble_sdk.config.YumaSDKConfig
import com.yuma.ble_sdk.data.ble.jdo.LeResponse
import com.yumacustomer.new_ble_sdk.data.CommonSessionConfig
import com.yumacustomer.new_ble_sdk.data.SmartSwapSubmitResponse
import com.yumacustomer.new_ble_sdk.data.SubmitType
import com.yumacustomer.new_ble_sdk.data.SwapStatusResultDto
import com.yumacustomer.new_ble_sdk.data.YumaResponse
import com.yumacustomer.new_ble_sdk.data.YumaResponse.Error
import com.yumacustomer.new_ble_sdk.data.YumaResponse.ResponseState
import com.yumaoem.core.utils.context.PlatformContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class YumaBleSDK constructor(appContext: Context, environment: String) {
    val context = appContext
    private var yumaSDK: OEMSDKComponent? = null
    private var isInitializedFlag = false
    private val sdkEnvironment: YumaSDKConfig.Environment = mapEnvironment(environment)

    companion object {
        private const val TAG = "YumaBleSDK"
    }

    private fun mapEnvironment(env: String): YumaSDKConfig.Environment {
        return when (env.uppercase()) {
            "DEV" -> YumaSDKConfig.Environment.DEV1
            "PREPROD", "PRE_PROD" -> YumaSDKConfig.Environment.PREPROD
            "PROD", "PRODUCTION" -> YumaSDKConfig.Environment.PROD
            else -> {
                Log.w(TAG, "Unknown environment: $env, defaulting to PROD")
                YumaSDKConfig.Environment.PROD
            }
        }
    }

   suspend fun initialize(sessionConfig: CommonSessionConfig, enableAnalytics: Boolean) {
        val sdkVersion = YumaSDK.getVersion()
        Log.d(TAG, "SDK Initialize :: sdkVersion: $sdkVersion, environment: $sdkEnvironment")

        yumaSDK = YumaSDK.getOEMInstance(
            context = context,
            environment = sdkEnvironment,
            oemEnvironment = sdkEnvironment,
            isAnalyticsEnabled = enableAnalytics
        )

        val androidSessionConfig = SessionConfig(
            userId = sessionConfig.userId,
            qrCode = sessionConfig.qrCode,
            accessToken = sessionConfig.accessToken,
            bikeName = sessionConfig.bikeName,
            checkInTime = sessionConfig.checkInTime,
            tokenId = sessionConfig.tokenId,
            batteryCount = sessionConfig.batteryCount,
            batteryType = sessionConfig.batteryType,
            swapType = sessionConfig.swapType,
            appVersion = sessionConfig.appVersion,
            applicationSourceId = sessionConfig.applicationSourceId,
            currentLatitude = sessionConfig.currentLatitude,
            currentLongitude = sessionConfig.currentLongitude,
            clientCityId = sessionConfig.clientCityId,
            clientVehicleId = sessionConfig.clientVehicleId,
            isMultiYcuSwap = sessionConfig.isMultiYcuSwap,
            partialCompletedCount = sessionConfig.partialCompletedCount,
            orderId = sessionConfig.orderId
        )

        Log.d(TAG, "SDK.init()")
        yumaSDK?.init(androidSessionConfig)
        isInitializedFlag = true
    }

   suspend fun startSwap(qrCode: String) {
        Log.d(TAG, "SDK.startSwap()")
        yumaSDK?.startSwap(qrCode)
    }

   suspend fun swapStatus(tokenId: Long) : SwapStatusResultDto {
        Log.d(TAG, "SDK.swapStatus()")
        val swapStatusResult = yumaSDK?.swapStatus(tokenId)
        return SwapStatusResultDto(
            swapStatus = swapStatusResult?.swapStatus ?: 0,
            updatedDt = swapStatusResult?.updatedDt ?: 0,
            isDoorOpen = swapStatusResult?.isDoorOpen ?: false,
            slotId = swapStatusResult?.slotId ?: 0
        )
    }

   suspend fun submit() {
        Log.d(TAG, "SDK.submit()")
        yumaSDK?.submit()
    }

   suspend fun clean() {
        Log.d(TAG, "SDK.clean()")
        yumaSDK?.clean()
        isInitializedFlag = false
    }

   suspend fun triggerAccessType() {
        Log.d(TAG, "SDK.setAccessType()")
        yumaSDK?.triggerAccessType()
    }

   fun getResponseFlow(): Flow<YumaResponse> {
        if (yumaSDK == null) {
            Log.e(TAG, "yumaSDK is null, returning empty flow")
        }
        return yumaSDK?.yumaResponse?.map { androidResponse ->
            mapAndroidResponseToCommon(androidResponse)
        } ?: emptyFlow()
    }

   fun isInitialized(): Boolean = isInitializedFlag

   suspend fun smartSwapSubmit() : SmartSwapSubmitResponse {
        Log.d(TAG, "SDK.smartSwapSubmit()")
        val smartSwapResult = yumaSDK?.smartSwapSubmit()
        Log.d(TAG, "SDK.smartSwapSubmit() result: $smartSwapResult")
        return SmartSwapSubmitResponse(
            id = smartSwapResult?.id ?: 0,
            message = smartSwapResult?.message ?: "",
            isManualFlowEnabled = smartSwapResult?.isManualFlowEnabled ?: false,
            timeTaken = smartSwapResult?.timeTaken ?: "",
            isTokenCompleted = smartSwapResult?.isTokenCompleted ?: false,
            isSessionTimedOut = smartSwapResult?.isSessionTimedOut ?: false,
            isCallInitiated = smartSwapResult?.isCallInitiated ?: false,
            isPingAvailable = smartSwapResult?.isPingAvailable ?: false,
            isYcuScanAllowedAgain = smartSwapResult?.isYcuScanAllowedAgain ?: false,
        )
    }

    private fun mapAndroidResponseToCommon(androidResponse: LeResponse): YumaResponse {
        Log.d(TAG, "SDK Response $androidResponse")
        return when (androidResponse) {
            is LeResponse.Error -> Error(
                code = androidResponse.code,
                message = androidResponse.message,
                id = androidResponse.id
            )

            is LeResponse.Connected -> YumaResponse.Connected
            is LeResponse.SubmitSuccess -> YumaResponse.SubmitSuccess(
                id = androidResponse.type.toAppSubmitType(),
                isMultiYCUSwap = androidResponse.isMultiYCUSwap,
                partialCompletedCount = androidResponse.partialCompletedCount
            )
            /**
            is LeResponse.SyncDifferenceRes -> {
            val commonSyncDiff = CommonSyncDifference(
            pickedCommonBatteryPortBin = androidResponse.syncDiff.pickedBatteryPortBin?.map {
            CommonBatteryPortBin(
            it.binNumber,
            it.port,
            it.soc
            )
            },
            droppedCommonBatteryPortBin = androidResponse.syncDiff.droppedBatteryPortBin?.map {
            CommonBatteryPortBin(it.binNumber, it.port, it.soc)
            },
            initialBatteryBinInfo = androidResponse.syncDiff.initialBatteryBinInfo?.map {
            CommonBatteryPortBin(it.binNumber, it.port, it.soc)
            },
            finalBatteryBinInfo = androidResponse.syncDiff.finalBatteryBinInfo?.map {
            CommonBatteryPortBin(it.binNumber, it.port, it.soc)
            },
            droppedBatteryBinId = androidResponse.syncDiff.droppedBatteryBinId
            )
            SyncDifferenceRes(commonSyncDiff)
            }
             **/
            is LeResponse.SessionCleared -> YumaResponse.SessionCleared
            is LeResponse.PermissionGranted -> YumaResponse.PermissionGranted
            is LeResponse.InitSuccess -> YumaResponse.InitSuccess
            is LeResponse.ResponseState -> ResponseState(androidResponse.cuResponse)
            is LeResponse.LeScan -> YumaResponse.LeScan
            is LeResponse.QuickScanning -> YumaResponse.QuickScanning
            is LeResponse.SDKInitialized -> YumaResponse.SDKInitialized
            is LeResponse.ScanningCompleted -> YumaResponse.ScanningCompleted
            is LeResponse.ServiceDiscovered -> YumaResponse.ServiceDiscovered
            is LeResponse.ConfigSet -> YumaResponse.ConfigSet
            is LeResponse.MultiYCUSwap -> YumaResponse.MultiYcuSwap(
                isMultiYcuSwap = androidResponse.isMultiYCUSwap,
                partialCompletedCount = androidResponse.partialCompletedCount
            )
        }
    }

    fun com.yuma.ble_sdk.data.ble.jdo.SubmitType.toAppSubmitType(): SubmitType {
        return when (this) {
            com.yuma.ble_sdk.data.ble.jdo.SubmitType.SWAP_SUBMIT -> SubmitType.SWAP_SUBMIT
            com.yuma.ble_sdk.data.ble.jdo.SubmitType.MANUAL_SWAP_SUBMIT -> SubmitType.MANUAL_SWAP_SUBMIT
        }
    }

   suspend fun submit(batteryQr: List<String>) {
        Log.d(TAG, "SDK.manualSwapSubmit($batteryQr)")
        yumaSDK?.manualSwapSubmit(batteryQr)
    }
}