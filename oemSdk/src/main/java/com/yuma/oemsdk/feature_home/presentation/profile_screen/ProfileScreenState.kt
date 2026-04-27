package com.yumaoem.feature_home.presentation.profile_screen

import com.yumaoem.core.model.auth.User
import com.yumaoem.feature_home.domain.model.profile.UserDetails
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import com.yumaoem.feature_home.presentation.profile_screen.components.UiSwapItem

data class ProfileScreenState(
    val user: User? = null,
    val userDetails:UserDetails? = null,
    val batteryDetails: List<BatteryDetails> = emptyList(),
    val swapHistory: List<UiSwapItem> = emptyList(),
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val page: Int = 1,
    val isSheetOpen: Boolean = false,
    val bikeProvider:String = "",
    val bikeNumber:String = "",
)

