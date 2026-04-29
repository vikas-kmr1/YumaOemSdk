package com.yumaoem.feature_onboarding.domain.use_case.verify_otp

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_onboarding.data.dto.verify_otp.request.SilentAuthRequest
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.SilentAuthResponse
import com.yumaoem.feature_onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class SilentAuthUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke(
        silentAuthRequest: SilentAuthRequest
    ): Flow<RestClientResult<SilentAuthResponse>> = repository.silentAuth(silentAuthRequest = silentAuthRequest)
}