package com.yumaoem.feature_onboarding.data.network

import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core_network.api.HttpClientApi
import com.yumaoem.core_network.impl.data.base.BaseDataSource
import com.yumaoem.feature_onboarding.data.dto.verify_otp.request.SilentAuthRequest
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.SilentAuthResponseDTO
import com.yumaoem.feature_onboarding.data.network.util.Endpoints
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class OnboardingRemoteDataSource(
    private val httpClientApi: HttpClientApi,
    private val locationProvider: CoreLocationProvider
): BaseDataSource() {
    suspend fun silentAuth(
        silentAuthRequest: SilentAuthRequest
    ) = getResult< SilentAuthResponseDTO> {
        val client = httpClientApi.getOnboardingHttpClient()
        val currentLocation = locationProvider.getCurrentLocation()
        client.post {
            url(Endpoints.VERIFY_OTP)
            setBody(silentAuthRequest)
            parameter("latitude", currentLocation?.latitude)
            parameter("longitude", currentLocation?.longitude)
        }
    }
}