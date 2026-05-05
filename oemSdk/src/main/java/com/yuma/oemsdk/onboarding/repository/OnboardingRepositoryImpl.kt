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
            /* .silentAuth(silentAuthRequest)*/
            FAKE_DATA //TODO remove this
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

val FAKE_DATA = RestClientResult.success(
    SilentAuthResponseDTO(
        accessToken = AccessToken(
            expiresIn = "1d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9BQ0NFU1NfVE9LRU4iLCJpYXQiOjE3Nzc4OTIxMzgsImFwcGxpY2F0aW9uX3NyY19pZCI6MjAsImV4cCI6MTc3Nzk3ODUzOH0.eZmp5FV4qxS6rRtMwJI84tI8kpXMt3gKUx9bpXuQMnk4-SvFxr8CPxwIYv0KQmgXoiSazHZjI8XTLHS_b1iRV4_t6LRRKYO8HZP0rLQtRHwF_8aW9sGzkB5Hup79qKv_46Id3KNymxjAxzVyvFWduWmL-oS4Rg9iEEo6phf6wlu8D-6knXUs7EKVG3vdrdPOC-NnMJmiKG88GyP_Trh8ftPv7RB03XlTZHc1SF3W29_Vcx5lzKaP-wzfE5DW8oj3X4S8TAd47qkKNlZSdIKbjpgVXzqt49b5c9woQH8NJu-dpywD4Ta5SSiDZL3NSC99vLBmisNJa5dGaDwYR7lmA1l_T9EurivrK2xBOSVSBbqEVvU8qsaibephVRX0ugMg7QvUlSnmrSmlUXiScTC4zQpPfNQAtX-8ItHKVt6xZw996saoPUd1vaqlxewcgMuhALcKuRxgbK5WHgq8rJPdo0DshhwZOXp6Bo3JRy33JJO_10Kd8jZZryaPDKfTAEFaILxJVKOdgLPs9eSB3oefWJ1cUycDtQPSrK9mKzFo6qPTYQP4ohqnMa7HOf6lIBxiogzsqxmsKS-pNK9nGoJH2pOoMBtb6ZWAy0IFWkgv-1WEgFhCg6IjJiNC_Ao6D4J2o8CzbzZZu0wit-Dq4LMnUMq-vz1mvd6IhmAEbTXbSxI"
        ),
        refreshToken = RefreshToken(
            expiresIn = "30d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9SRUZSRVNIX1RPS0VOIiwiaWF0IjoxNzc3ODkyMTM4LCJhcHBsaWNhdGlvbl9zcmNfaWQiOjIwLCJleHAiOjE3ODA0ODQxMzh9.dorkGb__jeCdFsjN2MkoxSDGVTRvAleEJTzyoTlLh2CkPSIbmk7_ck7bW_XQhhHONSNzLyimh-nlJ9HaI1KsVo042VI4Rs1oCEX91t8HJsTaNU2bDhmzudv3ZirKDSMuImFL29LF8XsBppAipmywsmwGsCGGC8P87HovsAWATSMkkcD1wbRf7ouy5WUgPuuV5jalTnPhXXgPQ1zd4hNWLKg0pXzzylhzFaMPmiHOp5LnZ_7vKAQZjf0SVFZ32WpiPBhVOEPr7KS9bFbRn88DKrtdctkUAK7WJystWnRQuvZXTPKmHoS4N3HeYIVDCiPqQw_WIpGWK-RB3ZcBmqbQocDWM2Pqm3EJy4LyIosnmePTQ6tknmg3mU-11E20osC7YMvEOPvICoVR_a-TjEA-L1wPsbssPpBKsvODf22B_CP_sXZdRYk6HuWW--S9g9y0E4q0t_7KtMOpWpVrLFWg8bX-Ll77GQoGuPMPiCdVbptGtO9H8rvxvCf1kzbNIsIOzcZKjg_Roz1AwfnXSHaQss63wbw3Hf-Ftji3RcxlcdwYelYIxpf-9BI6Tsp04u72oGjVLLJWw0ckNPj1OPFqVxtvFdU5AjP5RLCMbB7WDa-npRYXv3wnf-MEbtTL1mljVw-tfEhXqK3RiwA8bJPT2QQOT78F2e1b-i6ksdC0xcI"
        ),
        userInfo = UserInfoDTO(
            userId = "7537882",
            firstName = "Vikas",
            surname = "",
            phone = "8220822082",
            userStatusId = 1,
            isPrePaidUser = false,
            batteryCount = 2,
            clientDetails = listOf(
                ClientDetailsItem(organisationName = "Magna Yuma Pvt Ltd", clientId = 14)
            ),
            clientVehicles = listOf(
                ClientVehiclesItem(
                    bikeProvider = "B Gauss Mobility",
                    clientId = 7,
                    qrCode = "KG1A000795",
                    itemGroupId = 87,
                    bikeNumber = "TN29AB0795",
                    clientCityId = 8,
                    clientVehicleId = 843
                )
            ),
            currentClientCityIds = listOf(
                CurrentClientCityIdsItem(clientId = 7, clientCityId = 8)
            ),
            activeClientUsers = listOf(
                ActiveClientUsersDTO(clientUserId = 608, clientId = 7)
            )
        )
    )
)