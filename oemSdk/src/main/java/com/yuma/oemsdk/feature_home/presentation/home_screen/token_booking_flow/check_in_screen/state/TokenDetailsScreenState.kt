package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.state

import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import com.yumaoem.feature_home.domain.model.token_flow.beacon_details.Beacon
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails


data class TokenDetailsScreenState(
    val idDiySwap:Boolean = false,
    val expiryTime:String = "",
    val dialogState: DialogState = DialogState.None,
    val bookedTokenDetails: BookedTokenDetails? = null,
    val beaconDetails: List<Beacon>? = null,
    val isCheckInButtonLoading:Boolean = false,
    val isCancelBookingButtonLoading:Boolean = false,
    val currentBatteryDetails: List<BatteryDetails>? = null,

    // states used for battery verification before proceeding to diy swap
    val batteryVerificationCompleted: Boolean = false,
    val showBatteryVerificationScreen: Boolean = false,
    val isFlashLightOn: Boolean = false,
    val batteryQrList: List<String> = emptyList(),
    val totalBatteryCount: Int = 0,
    val isSubmitting: Boolean = false,
    val isBatteryVerificationRequired: Boolean = false
) {
    val isMultiBatteryFlow: Boolean
        get() = totalBatteryCount > 1

    val isLoading: Boolean
        get() = isSubmitting
}
