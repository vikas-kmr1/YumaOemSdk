package com.yumaoem.feature_onboarding.data.repository

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.core_network.impl.util.getFlowResult
import com.yumaoem.core_network.impl.util.mapFromDTO
import com.yumaoem.feature_home.domain.model.drop_off_data.DropOffScreenData
import com.yumaoem.feature_onboarding.data.dto.fcm_token.request.InsertFcmRequest
import com.yumaoem.feature_onboarding.data.dto.verify_otp.request.SilentAuthRequest
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.AccessToken
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.ActiveClientUsersDTO
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.ClientDetailsItem
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.ClientVehiclesItem
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.CurrentClientCityIdsItem
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.RefreshToken
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.SilentAuthResponseDTO
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.UserInfoDTO
import com.yumaoem.feature_onboarding.data.dto.verify_otp.response.toDomain
import com.yumaoem.feature_onboarding.data.network.OnboardingRemoteDataSource
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.SilentAuthResponse
import com.yumaoem.feature_onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class OnboardingRepositoryImpl(
    private val onboardingRemoteDataSource: OnboardingRemoteDataSource
) : OnboardingRepository {
    // repository will convert DTO domain model
    override suspend fun silentAuth(silentAuthRequest: SilentAuthRequest): Flow<RestClientResult<SilentAuthResponse>> =
        getFlowResult {
            onboardingRemoteDataSource
            .silentAuth(silentAuthRequest)
                .mapFromDTO { dto ->
                    dto.toDomain()
                }
        }

    override suspend fun insertFcmToken(
        request: InsertFcmRequest
    ): Flow<RestClientResult<Any>> = getFlowResult {
        onboardingRemoteDataSource.saveFcmToken(request)
    }

    override suspend fun getDropOffScreen(
        clientUserId: Int
    ): Flow<RestClientResult<DropOffScreenData>> = getFlowResult {
        onboardingRemoteDataSource.getDropOffScreen(clientUserId).mapFromDTO { dto ->
            dto.toDomain()
        }
    }
}