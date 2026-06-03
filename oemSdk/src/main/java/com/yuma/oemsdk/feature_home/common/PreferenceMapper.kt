package com.yumaoem.feature_home.common

import com.yumaoem.corepreference.model.BookedTokenDetailsDTO
import com.yumaoem.corepreference.model.LatLongDTO
import com.yumaoem.corepreference.model.YumaStationMarkerDTO
import com.yumaoem.corepreference.model.YumaStationStatusDTO
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationStatus
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.ChargingStationState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong

fun BookedTokenDetails.toDTO() = BookedTokenDetailsDTO(
    isDiyToken = isDiyToken,
    latitude = latitude,
    clientVehicleQrCode = clientVehicleQrCode,
    tokenNumber = tokenNumber,
    tokenStatusId = tokenStatusId,
    tokenID = tokenID,
    chargingStationId = chargingStationId,
    tokenExpiryTimeStamp = tokenExpiryTimeStamp,
    tokenBookingTimeStamp = tokenBookingTimeStamp,
    longitude = longitude,
    batteryType = batteryType,
    batteryCount = batteryCount,
    isNewDiyUser = isNewDiyUser,
    bookingStation = bookingStation?.toDTO(),
    isBatteryVerificationRequired = isBatteryVerificationRequired,
    isBatteryVerified = isBatteryVerified
)

fun BookedTokenDetailsDTO.toDomain() = BookedTokenDetails(
    isDiyToken = isDiyToken,
    latitude = latitude,
    clientVehicleQrCode = clientVehicleQrCode,
    tokenNumber = tokenNumber,
    tokenStatusId = tokenStatusId,
    tokenID = tokenID,
    chargingStationId = chargingStationId,
    tokenExpiryTimeStamp = tokenExpiryTimeStamp,
    tokenBookingTimeStamp = tokenBookingTimeStamp,
    longitude = longitude,
    batteryType = batteryType,
    batteryCount = batteryCount,
    isNewDiyUser = isNewDiyUser,
    bookingStation = bookingStation?.toDomain(),
    isBatteryVerificationRequired = isBatteryVerificationRequired,
    isBatteryVerified = isBatteryVerified
)


fun YumaStationMarker.toDTO() = YumaStationMarkerDTO(
    stationName = stationName,
    stationId = stationId,
    title = title,
    location = location.toDTO(),
    stationCurrentStatus = stationCurrentStatus.toDTO()
)

fun YumaStationStatus.toDTO() = YumaStationStatusDTO(
    stationStatusId = stationStatusId,
    stationState = stationState.name,
    currentTime = currentTime,
    nextOpeningTime = nextOpeningTime,
    nextClosingTime = nextClosingTime,
    distanceFromUser = distanceFromUser
)

fun LatLong.toDTO() = LatLongDTO(
    latitude = latitude,
    longitude = longitude
)

fun LatLongDTO.toDomain() = LatLong(
    latitude = latitude,
    longitude = longitude
)

fun YumaStationMarkerDTO.toDomain() = YumaStationMarker(
    stationName = stationName,
    stationId = stationId,
    title = title,
    location = location.toDomain(),
    stationCurrentStatus = stationCurrentStatus.toDomain()
)

fun YumaStationStatusDTO.toDomain() = YumaStationStatus(
    stationStatusId = stationStatusId,
    stationState = ChargingStationState.valueOf(stationState),
    currentTime = currentTime,
    nextOpeningTime = nextOpeningTime,
    nextClosingTime = nextClosingTime,
    distanceFromUser = distanceFromUser
)
