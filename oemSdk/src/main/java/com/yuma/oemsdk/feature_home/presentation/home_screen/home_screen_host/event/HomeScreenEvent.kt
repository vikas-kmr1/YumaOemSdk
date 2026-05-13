package com.yumaoem.feature_home.presentation.home_screen.home_screen_host.event


sealed class HomeScreenEvent {
    data object OnTokenBooked : HomeScreenEvent()
    data object OnCheckedInAtStation: HomeScreenEvent()
    data object OnBookingCancelled : HomeScreenEvent()
    data class OnSwapComplete(val swapTime:String) : HomeScreenEvent()
    data object OnSwapSuccessShown : HomeScreenEvent()
    data object OnDiySwapStarted : HomeScreenEvent()
    data object OnYcuQrScanned: HomeScreenEvent()
    data object NavigateToYcuScanScreen : HomeScreenEvent()
    data object OnCheckInReverted: HomeScreenEvent()
    data object NavigateToTagBattery : HomeScreenEvent()
    data object OnPartialSwapSuccess : HomeScreenEvent()
}