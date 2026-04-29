package com.yumaoem.feature_home.common.customer_support

import com.yumaoem.core.model.auth.User
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerDetail
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerRequest
import com.yumaoem.feature_home.domain.usecase.auto_dialer.AutoDialerRequestUseCase
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class CustomerSupportCallInteractor(
    private val prefsApi: YumaPrefUtilApi,
    private val autoDialerRequestUseCase: AutoDialerRequestUseCase
) {

    fun requestCall(contactNumber: String) = flow {
        val userDetails: User = prefsApi.getUserData()
            ?: run {
               return@flow
            }
        val tokenDetails = prefsApi.getBookedTokenDetails()

        val fullName = "${userDetails.firstName} ${userDetails.surname}".trim()

        val request = AutoDialerRequest(
            autoDialerDetails = listOf(
                AutoDialerDetail(
                    clientId = userDetails.clientId,
                    clientUserId = userDetails.clientUserId,
                    clientCityId = userDetails.clientCityId,
                    userName = fullName,
                    userPhoneNumber = contactNumber,
                    bikeNumber = userDetails.clientVehicleQrCode,
                    yumaClientTokenId = tokenDetails?.tokenID?.toIntOrNull(),
                    chargingStationId = tokenDetails?.bookingStation?.stationId,
                    batteryCount = tokenDetails?.batteryCount
                )
            )
        )

        emitAll(autoDialerRequestUseCase.invoke(request))
    }
}
