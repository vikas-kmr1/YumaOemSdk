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
            FAKE_DATA_PREPROD //TODO remove this
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

val FAKE_DATA_DEV = RestClientResult.success(
    SilentAuthResponseDTO(
        accessToken = AccessToken(
            expiresIn = "1d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9BQ0NFU1NfVE9LRU4iLCJpYXQiOjE3Nzg2NjkxMzcsImFwcGxpY2F0aW9uX3NyY19pZCI6MjAsImV4cCI6MTc3ODc1NTUzN30.NNBSdLDHWVowfFeT_I-Tqv3O675wEN5t9VF4TCtB3SIN1q_CqCLXuZM2gZHX4qFXN1D0SZ4B-bdOPXONDv3sSruUycqnuDtA7OgC-9ttZ58dfcL-8izQPTNewXmqYj_hbDQyEKDpX9DQKvan2OKJvDkMgSOer0_xMytJl3beVs_kP_YgpabTQTpXauUqH5_u83PYYldLpkPnJ7trxDLvvY34MiMhZiwFn6YEbHO7prZ0-8ooK7s7glLWWk2Px-c0x77EifqRdGOQO3zr2Fh7_WPbbrBJ6hX16Ksxsui_EshFAculyeUhT-UCNFMeOWXZT2c79FQyi7lopHgO0f8aFRuMgiVhGrjSW92PwCZIHj_EaGF2NW-vW9eSrhp1CNySit6uTBF59bENgWrqc8mC4s1uuy94EI3saDDrdGElJfMTzN1HGtbJBXAxTzHwKenycfv8RMf3gAuLhhMYu1EzwI9SFGHOkkFzeEZppXUzsCNd8hLeeY-qpkA8IcQoFMhqTiqoNoLy8PXN1ohSV3JHv-FI-meMCQvqnJo9CkGRAQ_1jZTtZ0-gmfzD9s_Dvw2cRTeDzTgEgK-8BUlMqS59G26R1P7AbRywWSBtdOh-v7UZxc8qL_dhatm9XOeTfxtFvV-GaxAw_goreAJ5IicGBRDlZ-oPUproXthwKtVTXD4"
        ),
        refreshToken = RefreshToken(
            expiresIn = "30d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9SRUZSRVNIX1RPS0VOIiwiaWF0IjoxNzc4NjY5MTM3LCJhcHBsaWNhdGlvbl9zcmNfaWQiOjIwLCJleHAiOjE3ODEyNjExMzd9.GdePTO41ydXO7iipvXlUE1k289OrncEH9OuRpJf-HyVSORyGSQpjhUy6uW0wuxLWVG-Ox6VFrF6_Q3V367NqjbpGQetblSmMr132WDmgUvHstxxGibVtLJDcAn7NNUB_yVCNXhrBaQFor7dhYeYlkfk-jEzgjLMhexCrmwzBryNEJxah7RoqHQ8KKM8sYsrpSwwlTs1xEI8zMLbtSnQr8CfdjlbrSHIUFxEk684ZVfh33geVT37d-5XOgU1GA0Ds0zRikBpLkO4cC00JIJYeNWshlWel7hoZbJ3NuLSFxKjrDczTkQnoA25cRdqs5yat8rl7mYZoT1QHEXMyTIy6kXKeX1LX6JPdJGis1W5ZtUF7RvT2_Gc5m0bUYSeAw6ZXtDVRi73CeSZw1Hf5ueRLwYLUyLSJdlJCtB4YLkk6yqeCBi5wK0RO3p55XZ6v7LEVUtBRTEpFIm1Lfwdrfi1BheuD4SGbGzbefgJiL5htFXKowm0ESbJ_4Ksej942OCsg2wBThxgfPy679EgNMXIUy40Ta9BEa1SPTaIS41tgx4fMUimtA264NeHh2sASi_7Hn4zx33FJuUBgMqVqTjCmpiUwgoe6KYtZxpzC1S0fgwCsAA2tOp7Ca7DG8KURbpYxWOeaNPETi9RfxSV_ZwiFMTKNVU66fpFD481z6-OdvcU"
        ),
        userInfo = UserInfoDTO(
            userId = "7537882",
            firstName = "AF TEST",
            surname = "",
            phone = "8220822082",
            userStatusId = 1,
            isPrePaidUser = true,
            batteryCount = 2,
            clientDetails = listOf(
                ClientDetailsItem(organisationName = "B Gauss Mobility", clientId = 7)
            ),
            clientVehicles = listOf(
                ClientVehiclesItem(
                    bikeProvider = "B Gauss Mobility",
                    clientId = 7,
                    qrCode = "KG1A000795",
                    itemGroupId = 76,
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


val FAKE_DATA_PREPROD = RestClientResult.success(
    SilentAuthResponseDTO(
        accessToken = AccessToken(
            expiresIn = "1d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NjE4NjIyIiwidHlwZSI6Ik9FTV9BQ0NFU1NfVE9LRU4iLCJpYXQiOjE3Nzg2NTgzODgsImFwcGxpY2F0aW9uX3NyY19pZCI6MjAsImV4cCI6MTc3ODc0NDc4OH0.c2MIbsbpkYcLUyCS0H6i_FIXB0ocHbJrJmj_-Oh91Vlw4-XK928Eha6QlEjw4-6NXWaw_0PN1vrjM001dRtShq9vHkzdcbD072EDXCfVWXCxG-MWAgx3sxKFVAi1rR5HCrpziETYuExIb-eWVhmJ1AshY7kQ-vqkG_A5uI7kBK0WVzRYTY0SE8PLoygTWZ5w9aoD7JMBg7powrRwl3bXAEJEEMBEiUOorZdEroZA9kOFCgVzjrnsm8MMK2PIUfSCSgbulPDQXpYfERLD8jRLYvkwJXHvX1_pEU2CbgPj0tz3zysEzDCKBzniIAmBLZHSJ00ittxOpjux4QJ_0IT_TQ"
        ),
        refreshToken = RefreshToken(
            expiresIn = "30d",
            token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NjE4NjIyIiwidHlwZSI6Ik9FTV9SRUZSRVNIX1RPS0VOIiwiaWF0IjoxNzc4NjU4Mzg4LCJhcHBsaWNhdGlvbl9zcmNfaWQiOjIwLCJleHAiOjE3ODEyNTAzODh9.p8J_AIKw4BiJR3IPkTy4VeztecOpzBFZTLepuZsDXqDX-_Pzz3uQc9gVijeGl4G89rRcLjP1O_GTUpYqt-WjCO4JRRKKMI_7C7NFAjLAxbf7GmKJivv8q08AVZEkKHf7cLLh9q_HkNUbHdqTd-u05arnX3RmvtZQMRn0s9cN36UAzq52XWXJGT3QBrt2PFomX8L5AJaUWKbmF4NZFRmT6VwaIXCBqd6oVjxXdUHcSsdIX3M-kXLXDVziBwXPywhs3j8ECQXh6a98iKHKS27VzwmLg2symNO1boR3TjXQP37uebKIl7dCGg5gfwBnw0SbysDBEvaIPBJKCu9Zu0y9dg"
        ),
        userInfo = UserInfoDTO(
            userId = "7618622",
            firstName = "chittiboyina Kavya",
            surname = "",
            phone = "9121385328",
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
                ActiveClientUsersDTO(clientUserId = 136, clientId = 14)
            )
        )
    )
)