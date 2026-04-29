package com.yumaoem.feature_onboarding.domain.repository

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.domain.model.drop_off_data.DropOffScreenData
import com.yumaoem.feature_onboarding.data.dto.fcm_token.request.InsertFcmRequest
import com.yumaoem.feature_onboarding.data.dto.verify_otp.request.SilentAuthRequest
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.SilentAuthResponse
import kotlinx.coroutines.flow.Flow


interface OnboardingRepository {
    suspend fun silentAuth(
        silentAuthRequest: SilentAuthRequest
    ): Flow<RestClientResult<SilentAuthResponse>>


    suspend fun insertFcmToken(
        request: InsertFcmRequest
    ): Flow<RestClientResult<Any>>

    suspend fun getDropOffScreen(
        clientUserId:Int
    ):Flow<RestClientResult<DropOffScreenData>>
}