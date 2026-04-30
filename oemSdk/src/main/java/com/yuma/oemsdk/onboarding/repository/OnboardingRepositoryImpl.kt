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
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9BQ0NFU1NfVE9LRU4iLCJpYXQiOjE3Nzc1NDkyODgsImFwcGxpY2F0aW9uX3NyY19pZCI6MjAsImV4cCI6MTc3NzYzNTY4OH0.SRGd73Q6L50jGKOIeOP84liaH4Of_AIef8qArpnUhjA6CXDSjylkXMWmHyMI0A9WqflbdZYYRfaItaayUPPVmGuQIbokKvxE9MGlTmYXCntVxnD85u6vj8alNNdCaqOhHQXEW13xeSgq210B19xqLurGBYkaI2F8f7aAvYlOU3YYwyuwpeiTcNaSrlA741blRuu2NsvF3LCl6GzPrvTZpGS1SQyrm-YEn1MnxHY1mXYIUDbsKnhWAP4MqCJOW6XGFXkBb6htUN_T8u1BGeAtNzvnrN7LdLj71IGQd1jVSVy0egGEw7wLSREO8-dHFRhaqoLH3JDhRDx64ehHgKLPls1Te1p_PfZpMxMWIG_NWmlOprz7xPmWe5g-8nctbsVBwfVcnWrCVtGKQef09LrCEytPzRHwV28TaFYH-NiVSdtg9tlcEGFxz3sYd7Vo-IMHb5sjpvmpcp5-60h6t2n2zp1h16fh2feGcASWxg6NscHZtzZIG3EB_JBv2rqZyq5kZ6LpybCUOTZ_09lFxkBn-l0jdj2w97q-ufzt3aF2rpSpBocqLzhSFy7xf61lMG864TM6juOWRQSYIxkXMnSaqVXQ8ScZuFu5bzuFVAkiP4HDo445lXB97cpk3PuuOZxUs5oSau7mHvD3TqR_6w6bqyJf5ez-Tz-NuBmbslnbBfQ"
        ),
        refreshToken = RefreshToken(
            expiresIn = "30d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9SRUZSRVNIX1RPS0VOIiwiaWF0IjoxNzc3NTQ5Mjg4LCJhcHBsaWNhdGlvbl9zcmNfaWQiOjIwLCJleHAiOjE3ODAxNDEyODh9.QvK0Vk4eq0Im7COrnKrBgKz6iJQ3SN6xhIHz72ND3Xnzb5vjUCVxQQv_5HJncnG5TiqH8Vo4wScJW-10JB6SLr6DfYPJt0-K9Lt_3wxCwC9Frcr1YwkVebvfA93eUVd2u32jntR1JGbJaWgKV8EKogoKI8ZnklTUpSYjy6gnDjlrjDxyo9rqAWVSNXU34xUI9o_R4lKV01_FuwlBgFHm3dAhKduSgsBGNH27Tj9BL4cTI9wCHlLVzLd_A8TMp3ySC9o_TQbjm5kQFi9b70d2v0auOvnuQKIl6geJ-Vzfmv3AfkQYEVz-Otqrtx-Ldb8jDJza6nMaYTWfytRg4vh17lnQ0ruAijVybMQw5xVvxzTTaZvipmQv_sLslSHKY0V6bmNRwOTGYq0LjvJR05SffB9DpgQbyo6FxyuUgfj9DzBBRtpJQcvL8pejuZDD5zowxs7VVDugvptWNrw63Zsoc63LBRKvQpRo2GKlQeoRT6L9cp5HVZj1zGlPCm96_GEJSRqDetLuS-4jPiv8KW8iOcBKArRdS7ny9kTUYpCk6PpXYWKbfsk61koaNZv4sw2KeYOhA0B6MwDgEyeisKODE23kcHW0Pm3Q7F_zU5t821mwDXk-gHOJdpu9G7i_jxfK--L-otHVmmTSjNWD1Q6tnJZsPLmlPB0Ln3Ku_mHhIRQ"
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