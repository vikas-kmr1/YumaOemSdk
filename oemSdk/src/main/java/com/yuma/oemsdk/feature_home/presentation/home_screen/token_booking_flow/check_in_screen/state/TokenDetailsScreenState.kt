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
    val currentBatteryDetails: List<BatteryDetails>? = null
)
