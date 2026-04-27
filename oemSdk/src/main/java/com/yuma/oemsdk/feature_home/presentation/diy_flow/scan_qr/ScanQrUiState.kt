package com.yumaoem.feature_home.presentation.diy_flow.scan_qr

import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapBottomSheet

data class ScanQrUiState(
    val isFlashlightOn: Boolean = false,
    val isScanning: Boolean = true,
    val bottomSheet: ScanQrUiStateBottomSheet = ScanQrUiStateBottomSheet.None,
    val scannedQrCode: String? = null,
    val showIllustrationScreen: Boolean = false,
    val isQrScan: Boolean = true
)

sealed class ScanQrUiStateBottomSheet {
    object None : ScanQrUiStateBottomSheet()
    object IncorrectQRModalBottomSheet : ScanQrUiStateBottomSheet()
    data class GetCallbackBottomSheet(
        val mobileNumber: String,val isLoading:Boolean
    ): ScanQrUiStateBottomSheet()
}
