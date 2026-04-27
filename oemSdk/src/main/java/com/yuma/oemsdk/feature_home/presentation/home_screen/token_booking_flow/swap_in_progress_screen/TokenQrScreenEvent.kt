package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen

sealed class TokenQrScreenEvent {
    data object DismissDialog : TokenQrScreenEvent()
    data object CancelBookingClicked : TokenQrScreenEvent()
    data object CancelBookingConfirmed : TokenQrScreenEvent()
}