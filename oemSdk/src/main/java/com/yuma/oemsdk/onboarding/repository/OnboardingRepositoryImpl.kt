package com.yumaoem.feature_onboarding.data.repository

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.core_network.impl.util.getFlowResult
import com.yumaoem.core_network.impl.util.mapFromDTO
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
}

val FAKE_DATA = RestClientResult.success(
    SilentAuthResponseDTO(
        accessToken = AccessToken(
            expiresIn = "1d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NjQ1NzI4IiwidHlwZSI6Ik9FTV9BQ0NFU1NfVE9LRU4iLCJpYXQiOjE3NzcyODY0MjksImFwcGxpY2F0aW9uX3NyY19pZCI6MjAsImV4cCI6MTc3NzM3MjgyOX0.JtKAx9TywQiLwUSpQv-4NaHiYUJnvKM6D9mWPHtZVUgb6pG3r6HRt2CJ6zfTMGU6X9K4K3Nud0KWYr1v6D0mf8Q9Igs_AxlClhyScU1MR6sCHacCxK4UzbCaUznDx1IA9QqmkChDRxpFzFkhwahgOVEHeX_JA9Pw6hVM7jbvGgmut15vePDeaOnsMFhp_-07IjMVUNlx5cS7S50w81ncMB8JSSiscJedducqE_MGgnxmysBJC9qLgJ5Oj52lOMgIUv71a2Vz447N0BjkR67ntbun6Jz829VsCTNVGcHpAaJoH_ilIcByKVxlgbd5wJ8iznGTFeUhFj3k-cFyzg9pSw"
        ),
        refreshToken = RefreshToken(
            expiresIn = "30d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NjQ1NzI4IiwidHlwZSI6Ik9FTV9SRUZSRVNIX1RPS0VOIiwiaWF0IjoxNzc3Mjg2NDI5LCJhcHBsaWNhdGlvbl9zcmNfaWQiOjIwLCJleHAiOjE3Nzk4Nzg0Mjl9.Q3pEiycr3ZmoNCoEv-goPw7aebcgq3B_udEIIy_nWxtYBssd5yBQ_h1yhDLkX7EbhRY610BLW_4oxlu4CPdcdE2ZeJ0aj1CntbvqfZALAsqYeFTWr7igh-0wIl8jUf3gYfLrRI07u0asT2jPH8KBWYYppREVGEWSWHfb-bdgRznT0DzSG-2ckmV3ltuZEe-SKWvQNruEDT3686lBb54KBLpK72e8Hiopf7i_NhpTVZUs92HkvNk2YcZOrLOuMfpkIGorpNuzeQClihq6qCYBTE9Ib3Ogk0pltdruD6awlEd8-YFJb9a0fzCOBvcsVWe5C_rUTjVdCplaiJebFuZKOw"
        ),
        userInfo = UserInfoDTO(
            userId = "7645728",
            firstName = "Gowtham Reddy Bonala",
            surname = "",
            phone = "6300238739",
            userStatusId = 1,
            isPrePaidUser = false,
            batteryCount = 2,
            clientDetails = listOf(
                ClientDetailsItem(organisationName = "Magna Yuma Pvt Ltd", clientId = 14)
            ),
            clientVehicles = listOf(
                ClientVehiclesItem(
                    bikeProvider = "Magna Yuma Pvt Ltd",
                    clientId = 14,
                    qrCode = "YMTB000001",
                    itemGroupId = 87,
                    bikeNumber = "KA01HP1235",
                    clientCityId = 9,
                    clientVehicleId = 767
                )
            ),
            currentClientCityIds = listOf(
                CurrentClientCityIdsItem(clientId = 14, clientCityId = 16)
            ),
            activeClientUsers = listOf(
                ActiveClientUsersDTO(clientUserId = 237, clientId = 14)
            )
        )
    )
)