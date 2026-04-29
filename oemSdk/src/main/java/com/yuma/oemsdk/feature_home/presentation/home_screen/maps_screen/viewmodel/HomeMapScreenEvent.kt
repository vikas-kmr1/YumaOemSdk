package com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel

import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker

sealed class HomeMapScreenEvent {
    data class StationSelectedFromCarousel(val station: YumaStationMarker) : HomeMapScreenEvent()
    data class StationSelected(val station: YumaStationMarker) : HomeMapScreenEvent()
    data object OnMapLoaded : HomeMapScreenEvent()
    data object OnConfirmBookingClicked : HomeMapScreenEvent()
    data object OnDismissBookingConfirmationDialog : HomeMapScreenEvent()
    data object ShowBookingConfirmationDialog : HomeMapScreenEvent()
    data class GetDirectionsClicked(val station: YumaStationMarker) : HomeMapScreenEvent()
    data object OnBreakFinished : HomeMapScreenEvent()
}