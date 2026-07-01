package com.yumaoem.feature_home.presentation.diy_flow

import com.yumacustomer.new_ble_sdk.data.CommonSessionConfig
import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core.utils.device_info.getAppVersion
import com.yumaoem.core.utils.orZero
import com.yumaoem.corepreference.api.YumaPrefUtilApi

class CommonSessionConfigFactory(
    private val prefUtil: YumaPrefUtilApi,
    private val locationManager: CoreLocationProvider
) {
    suspend fun create(
        qrCode: String,
        bikeName: String,
        checkInTime: Long,
        tokenId: Long,
        batteryCount: Int,
        batteryType: Int,
        isMultiSwapYcu: Boolean? = null,
        partialCompletedCount: Int ?= null,
    ): CommonSessionConfig {
        val userData = prefUtil.getUserData()
        val location = locationManager.getCurrentLocation()
        val userId = userData?.userId
        val accessToken = prefUtil.getAccessToken()
        val clientCityId = userData?.clientCityId.orZero()
        val orderId = prefUtil.getOrderId()
        val applicationSourceId = "16"
        val uuid = ""
        val swapType = 5
        val clientVehicleId = userData?.clientVehicleId.orZero()
        return CommonSessionConfig(
            userId = userId.toString(),
            qrCode = qrCode,
            accessToken = accessToken.toString(),
            bikeName = bikeName,
            checkInTime = checkInTime,
            uuid = uuid,
            tokenId = tokenId,
            batteryCount = batteryCount,
            batteryType = batteryType,
            swapType = swapType,
            appVersion = getAppVersion(),
            applicationSourceId = applicationSourceId,
            currentLatitude = location?.latitude.orZero().toString(),
            currentLongitude = location?.longitude.orZero().toString(),
            clientCityId = clientCityId,
            clientVehicleId = clientVehicleId,
            isMultiYcuSwap = isMultiSwapYcu,
            partialCompletedCount = partialCompletedCount,
            orderId = orderId,
        )
    }
}