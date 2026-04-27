package com.yumaoem.feature_home.common.util.analytics_utils

import com.yumaoem.core.utils.currentTimeMillis
import com.yumaoem.core.utils.orZero
import com.yumaoem.corepreference.api.YumaPrefUtilApi

/**
 *  Used only for DIY & Battery Scan events.
 *  Token data is available only after booking.
**/

class CommonAnalyticsParamsProvider(
    private val prefUtilApi: YumaPrefUtilApi,
) {
    suspend fun get(): Map<String, Any> {

        val currentUser = prefUtilApi.getUserData()
        val tokenData = prefUtilApi.getBookedTokenDetails()

        return mapOf(
            "user_id" to currentUser?.userId.orEmpty(),
            "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
            "mobile_number" to currentUser?.phone.orEmpty(),
            "timestamp" to currentTimeMillis(),
            "station_id" to tokenData?.bookingStation?.stationId.orZero(),
            "station_name" to tokenData?.bookingStation?.stationName.orEmpty(),
            "token_id" to tokenData?.tokenID.orEmpty(),
            "bike_qr_number" to tokenData?.clientVehicleQrCode.orEmpty(),
            "fleet_name" to currentUser?.bikeProvider.toString(),
        )
    }
}
