package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen

sealed class TokenDetailsScreenEvent {
    data object DismissDialog : TokenDetailsScreenEvent()
    data object BookingExpiryTryAgainClicked : TokenDetailsScreenEvent()
    data object CancelBookingClicked : TokenDetailsScreenEvent()
    data object CancelBookingConfirmed : TokenDetailsScreenEvent()
    data object CheckInAtStationClicked : TokenDetailsScreenEvent()
    data object GetDirectionsClicked: TokenDetailsScreenEvent()
    data object NoBeaconFoundRetry: TokenDetailsScreenEvent()
}