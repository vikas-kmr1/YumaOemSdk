package com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.args

data class SwapInProgressScreenArgs(
    val checkInTime:Long,
    val ycuQrCode:String? = null,
    val isSwapInitiated: Boolean = false
)
