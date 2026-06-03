package com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress

import com.yumacustomer.new_ble_sdk.data.CommonSyncDifference
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails

data class DiySwapInProgressState(
    val swapInProgress: Boolean = false,
    val bottomSheet: DiySwapBottomSheet = DiySwapBottomSheet.None,
    val dialog: DiySwapDialog = DiySwapDialog.None,
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val swapDifference: CommonSyncDifference? = null,
    val removedBatteries: List<String>? = emptyList(),
    val insertedBatteries: List<String>? = emptyList(),
    val error: String? = null,
    val status: String = "Ready",
    val checkedInTime:Long = 0L,
    val ycuQrCode:String = "",
    val batteryCount:Int = 0,
    val isSubmitButtonVisible: Boolean = false,
    val batteryQrList: List<String> = emptyList(),
    val isManualEntry: Boolean = false,
    val stationName:String = "",
    val stationId:String = "",
    // scan battery screen
    val isSwapInitiated:Boolean = false,
    val showScanBatteryScreen:Boolean = false,
    val isBatteryScanEnabled: Boolean = true,
    val isFlashlightOn: Boolean = false,
    val showBikeDetailsBottomSheet: Boolean = false,
    val bikeDetails: BikeDetails = BikeDetails(),
    val isMultiYcuSwap: Boolean = false,
    val currentBatteryIndex: Int = 1
)

data class BikeDetails(
    val batteryDetails: List<BatteryDetails> = emptyList(),
    val bikeProvider: String = "",
    val bikeNumber: String = "",
    val bikeQrNumber: String = ""
)

sealed class DiySwapBottomSheet {
    object None : DiySwapBottomSheet()
    object SomethingWentWrong : DiySwapBottomSheet()
    object BluetoothConnectionFailed : DiySwapBottomSheet()
    data class GetCallbackBottomSheet(val mobileNumber: String,val isLoading:Boolean): DiySwapBottomSheet()
    object NoChargedBatteryFound: DiySwapBottomSheet()
    object SubmitFailureScanAgain: DiySwapBottomSheet()
    object DBInsertFailed: DiySwapBottomSheet()
    object DBInsertFailedCustomerSupport: DiySwapBottomSheet()
    object CBOpenFailed: DiySwapBottomSheet()
    object SwapStatusNotCompleted: DiySwapBottomSheet()
    object RemoteSwapInProgress: DiySwapBottomSheet()

    object DBDoNotInsertBatteryModalSheet: DiySwapBottomSheet()
}

sealed class DiySwapDialog {
    object None: DiySwapDialog()
    object MultiYcuSwapDialog: DiySwapDialog()
    object SwapInfoDialog: DiySwapDialog()
}