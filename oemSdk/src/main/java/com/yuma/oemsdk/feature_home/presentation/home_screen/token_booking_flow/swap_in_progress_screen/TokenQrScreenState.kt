package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen

data class TokenQrScreenState(
    val tokenId:Int = 0,
    val tokenNumber:Int = 0,
    val bikeNumber:String = "",
    val tokenQRCodeData:String = "",
    val isSwapInProgress:Boolean = false,
    val cancelBookingDialogVisible:Boolean = false,
    val isCancelBookingButtonLoading: Boolean = false,
    val tokenStatusUpdatedForStatus:Int = -1,
    val isSwapStartedEventSent:Boolean = false,
    val isSwapCompletedEventSent:Boolean = false
)
