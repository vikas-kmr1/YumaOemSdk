package com.yumaoem.feature_onboarding.domain.use_case.insert_fcm

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_onboarding.data.dto.fcm_token.request.InsertFcmRequest
import com.yumaoem.feature_onboarding.data.dto.fcm_token.response.InsertFCMDTO
import com.yumaoem.feature_onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class InsertFcmUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke(
        request: InsertFcmRequest
    ): Flow<RestClientResult<Any>> = repository.insertFcmToken(request)
}