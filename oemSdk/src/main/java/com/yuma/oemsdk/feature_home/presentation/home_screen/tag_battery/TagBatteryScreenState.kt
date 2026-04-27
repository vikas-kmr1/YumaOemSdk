package com.yumaoem.feature_home.presentation.home_screen.tag_battery

data class TagBatteryScreenState(
    val batteryQrList: List<String> = emptyList(),
    val bottomSheet: TagBatteryBottomSheet = TagBatteryBottomSheet.None,
    val isSubmitting: Boolean = false,
    val isFlashLightOn: Boolean = false,
    val totalBatteryCount: Int = 0,
    val scannedResult: String? = null,
    val isQrScan: Boolean = true,
    val error: String? = null
) {
    val isMultiBatteryFlow: Boolean
        get() = totalBatteryCount > 1

    val isLoading: Boolean
        get() = isSubmitting
}


sealed class TagBatteryBottomSheet {
    object None : TagBatteryBottomSheet()
    data class GetCallbackBottomSheet(val mobileNumber: String,val isLoading:Boolean): TagBatteryBottomSheet()
}