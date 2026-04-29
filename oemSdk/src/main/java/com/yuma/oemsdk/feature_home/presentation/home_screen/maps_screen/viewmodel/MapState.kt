package com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel

import com.yumaoem.core.model.auth.User
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.ChargingStationState

data class MapState(
    val userDetails: User? = null,
    val userLocation: LatLong? = null,
    val carouselStations: List<YumaStationMarker> = emptyList(),
    val selectedPath: List<LatLong>? = null,
    val selectedStation: YumaStationMarker? = null,
    val currentRouteStation: YumaStationMarker? = null,
    val isMapLoaded: Boolean = false,
    val bookingDialogState: BookingDialogState = BookingDialogState.Hidden,
    val bookingInProgress: Boolean = false,
    val launchTimeStamp: Long? = 0L,
    val numberOfStationsSelected:Int = 0,
    val batteryDetails: List<BatteryDetails> = emptyList(),
)

sealed class BookingDialogState {
    data object Hidden : BookingDialogState()
    data object Showing : BookingDialogState()
    data object BookingInProgress : BookingDialogState()
    data object SubscriptionExpired: BookingDialogState()
    data object PurchasePlanRequired: BookingDialogState()
}

data class YumaStation(
    val id: Int,
    val location: LatLong,
    val name: String,
    val stationClosingTime: String? = null,
    val stationOpeningTime: String? = null,
    val stationState: ChargingStationState = ChargingStationState.OPEN,
    val distanceFromUser: Int = 0,
)

sealed class BookingError {
    data object TagBattery: BookingError()
    data object SubscriptionExpired : BookingError()
    data object PurchasePlanRequired : BookingError()
    data class Unknown(val message: String) : BookingError()
}

fun mapBookingError(errorMessage: String): BookingError {
    return when {
        errorMessage.contains("Cannot book battery", ignoreCase = true) ->
            BookingError.SubscriptionExpired
        errorMessage.contains("Purchase a plan", ignoreCase = true) ->
            BookingError.PurchasePlanRequired

        errorMessage.contains("Tag battery", ignoreCase = true) ->
            BookingError.TagBattery
        else -> BookingError.Unknown(errorMessage)
    }
}
