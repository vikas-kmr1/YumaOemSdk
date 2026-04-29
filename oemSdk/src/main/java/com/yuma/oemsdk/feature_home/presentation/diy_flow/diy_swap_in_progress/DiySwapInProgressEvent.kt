package com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress

sealed class DiySwapInProgressEvent {
    data object OnSubmitClicked : DiySwapInProgressEvent()
    data object OnBackClicked : DiySwapInProgressEvent()
    data class OnBatteryScanned(val batteryQr: String, val isManualEntry: Boolean = false) : DiySwapInProgressEvent()
    data object ToggleFlashlight: DiySwapInProgressEvent()
    data object RetryBatteryScan: DiySwapInProgressEvent()
    data object ToggleBikeDetailsBottomSheet: DiySwapInProgressEvent()
}
