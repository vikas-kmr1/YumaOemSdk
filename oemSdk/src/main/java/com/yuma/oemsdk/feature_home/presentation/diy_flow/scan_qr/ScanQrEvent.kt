package com.yumaoem.feature_home.presentation.diy_flow.scan_qr

sealed interface ScanQrEvent {
    object Disposed: ScanQrEvent
    object ToggleFlashlight : ScanQrEvent
    object OnBackClicked : ScanQrEvent
    object DismissBottomSheet: ScanQrEvent
    object OnCustomerSupportClicked: ScanQrEvent
    data class OnScanCompleted(val result: String, val isQrScan: Boolean = false) : ScanQrEvent
    data class OnAutoDialerRequestReceived(val contactNumber: String) : ScanQrEvent
}
