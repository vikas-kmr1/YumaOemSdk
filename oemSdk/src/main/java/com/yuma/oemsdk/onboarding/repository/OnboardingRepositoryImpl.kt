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
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9BQ0NFU1NfVE9LRU4iLCJpYXQiOjE3Nzc0NDQ4OTYsImFwcGxpY2F0aW9uX3NyY19pZCI6MjAsImV4cCI6MTc3NzUzMTI5Nn0.LSbxVtBqqPjW-IL7Tf2pFTm0TKHODLrNyNCqUFhovnSs6iLPE-O3Ghk_jvkzZ1e4EQ2nw6cYi3xJ1GXxqlqB5njs2zzB57k9gFd5qzHr66yjr1Nb8lQJm7YC_UE1CBZZ5XIGI4eIxRlJv-Z0wSFD_72y_VKiG5ahzfattO9SFWxpW7SzoIzrsk_mnE4K3rdKFKkYNwRh3ExaELERWMn3jhnhEcyhZporAaCyk1rGcQpBZzs3ZT9wTvlfqOnbpcqnrYGJNg2-_4XPwE-FRJaQy0RzlW3vFaOrWtOiH0iHSY6mnLIw2WPCyzExD4B8Cj2PznGy7nCajwAnB3rlzzy8E7bIJs2LPSmYVg7PRU72DXyVajNCHanFfuq9yJ3gRCL_zcXd2x5D_IGCukfCtSttakihPyZGmHHaTYX5M9JE1hZv-2UjLGwY_IszO9eqOIeyskAFeRZDvlYK879-2ydoJu6gdS4xHjvTKJdEjZkDloQwldqByg4jaQJSoO4vXteust1IAoOmJ1CaXZy3knR1Aryez5Ec_4JfDLVv2lRwccq6gSNPTwE_1DxHZs7zn7KcMeyJ2J-5P7NjIl4EZZRldkbWY2iDojNxU83fqYwH4kbTgfoqnLN6uqBGkuFkgloQ4gkYQmIdYtLKBQ_vCNr1VqUkoDvgRKjm8TJnWE9vW5Q"
        ),
        refreshToken = RefreshToken(
            expiresIn = "30d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9SRUZSRVNIX1RPS0VOIiwiaWF0IjoxNzc3NDQ0ODk2LCJhcHBsaWNhdGlvbl9zcmNfaWQiOjIwLCJleHAiOjE3ODAwMzY4OTZ9.iveRhaMqDKgZvUYnq0LUgrlUHnv_Y8hdvNqtsHF6DPFMU8NbqW6qujifaFfG1qIkQ8XyEyAhc_QFwH740swOMSy8OlEhvvdavHzgdN9Mubixo4pdBTRj7o2YKRdxE90JfjSO3R6V79Pe3lcKch3LAqOVuKJveceXa-0IEE-png2V5pTbcUW3BWJGAlkK0ak8hhpb61Prv4aT5g1gd-OylqN3SKbogkVfFwRwF3G1sY-qt6u3jSDdoFQJ507xqs_NYxLE-IXCL7c_K59eWOEboO6KvCKIqzoX1ErwtyjiA8N6IaLEpbAH5ZqoRXRMFJRG8HL_P1IkaxynEZyNlmPj0zTCznpX_xA60-BxCDH7585_B5Av4sBY9SKMqC1yvR65zRcDfViAuCV27vs--8vkFOql3SUEOvk_plbqIce5xY1yL4zbouLJo_AHA9CIfUhGch3sah7Rdn66KcezJwm3Mom5CFBz4dMuw0PbjWo2Np23j6ZGdypLHvVlM4JMrdK0kpq_YDdaHn9Bmm9g0rLGcZ4E4k3ubzzE23MwETAA0IEfDGN6ucE49kXOa_AIPNd7QVe6lu54zpWKxWFHoyKNnYtmzNdvjuGvojDnpyF9pM3LC8XFyCn4JS31otnAnNVUOSv4Flj30_tO1MBgWk6nBzkjDim4TgYDQevdfHgO7G4"
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