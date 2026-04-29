package com.yumaoem.feature_onboarding.data.dto.verify_otp.response

import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.ClientDetails
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.CurrentClientCityIds
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.Token
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.User
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.*

fun SilentAuthResponseDTO.toDomain(): SilentAuthResponse = SilentAuthResponse(
    accessToken = accessToken.toDomain(),
    refreshToken = refreshToken.toDomain(),
    user = userInfo.toDomain()
)

fun AccessToken.toDomain(): Token = Token(
    expiresIn = expiresIn,
    token = token
)

fun RefreshToken.toDomain(): Token = Token(
    expiresIn = expiresIn,
    token = token
)

fun UserInfoDTO.toDomain(): User = User(
    id = userId,
    phone = phone,
    firstName = firstName,
    surname = surname.orEmpty(),
    isPrePaidUser = isPrePaidUser,
    userProfileUrl = "", // Not available in DTO
    currentClientCityIds = currentClientCityIds.map { it.toDomain() },
    clientDetails = clientDetails.map { it.toDomain() },
    clientVehicles = clientVehicles.map { it.toDomain() },
    activeClientUsers = activeClientUsers.map { it.toDomain() },
    batteryCount = batteryCount
)

fun CurrentClientCityIdsItem.toDomain(): CurrentClientCityIds = CurrentClientCityIds(
    clientId = clientId,
    clientCityId = clientCityId
)

fun ClientDetailsItem.toDomain(): ClientDetails = ClientDetails(
    organisationName = organisationName,
    clientId = clientId
)

fun ClientVehiclesItem.toDomain(): ClientVehicles = ClientVehicles(
    itemGroupId = itemGroupId,
    qrCode = qrCode,
    clientId = clientId,
    clientCityId = clientCityId,
    clientVehicleId = clientVehicleId,
    bikeProvider = bikeProvider,
    bikeNumber = bikeNumber
)

fun ActiveClientUsersDTO.toDomain(): ActiveClientUsers = ActiveClientUsers(
    clientUserId = clientUserId,
    clientId = clientId
)
