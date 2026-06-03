package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.state

sealed class DialogState {
    data object NoBeaconFound : DialogState()
    data object None : DialogState()
    data object ReachStation : DialogState()
    data object CancelBookingConfirmation : DialogState()
    data object TokenExpired : DialogState()
    data object WrongBattery: DialogState()
    data object ScanBattery: DialogState()
    data class CustomerSupport(val mobileNumber: String, val isLoading: Boolean) : DialogState()
}