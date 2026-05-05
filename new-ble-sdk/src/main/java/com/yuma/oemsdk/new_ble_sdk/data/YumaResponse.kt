package com.yumacustomer.new_ble_sdk.data

sealed class YumaResponse {
    data class Error(
        val code: String,
        val message: String,
        val id: Int? = null
    ) : YumaResponse()

    object PermissionGranted : YumaResponse()

    object LeScan : YumaResponse()
    object QuickScanning : YumaResponse()
    object ScanningCompleted : YumaResponse()

    object Connected : YumaResponse()
    object SessionCleared : YumaResponse()
    object ServiceDiscovered : YumaResponse()

    object NetworkSuccess : YumaResponse()

    object SDKInitialized : YumaResponse()

    data class SubmitSuccess(val id: SubmitType, val isMultiYCUSwap: Boolean?, val partialCompletedCount: Int?) : YumaResponse()

    data class SyncDifferenceRes(val syncDiff: CommonSyncDifference) : YumaResponse()
    data class ResponseState(val cuResponse: String) : YumaResponse()

    object InitSuccess : YumaResponse()

    object ConfigSet : YumaResponse()

    data class MultiYcuSwap(val isMultiYcuSwap: Boolean, val partialCompletedCount: Int): YumaResponse()

    companion object {
        // Error codes
        const val ERROR_PERMISSION_NOT_GRANTED = "PERMISSION_NOT_GRANTED"
        const val ERROR_QUICK_SCANNING_FAILED = "QUICK_SCANNING_FAILED"
        const val ERROR_CONNECTION_FAILED = "CONNECTION_FAILED"
        const val ERROR_DISCONNECTED = "DISCONNECTED"
        const val ERROR_NETWORK_ERROR = "NETWORK_ERROR"
        const val ERROR_SUBMIT_FAILED = "SUBMIT_FAILED"
        const val ERROR_SYSTEM_SYNC_FAILED = "SYSTEM_SYNC_FAILED"
        const val ERROR_ACCESS_TYPE_FAILED = "ACCESS_TYPE_FAILED"
        const val ERROR_SWAP_UNAVAILABLE = "SWAP_UNAVAILABLE"

        fun permissionNotGranted(message: String = "Required permissions not granted") = Error(ERROR_PERMISSION_NOT_GRANTED, message)
        fun quickScanningFailed(message: String = "Quick scanning failed") = Error(ERROR_QUICK_SCANNING_FAILED, message)
        fun connectionFailed(message: String = "Connection failed") = Error(ERROR_CONNECTION_FAILED, message)
        fun disconnected(message: String = "Device disconnected") = Error(ERROR_DISCONNECTED, message)
        fun networkError(message: String = "Network error occurred") = Error(ERROR_NETWORK_ERROR, message)
        fun submitFailed(message: String = "Submit operation failed", id: Int? = null) = Error(ERROR_SUBMIT_FAILED, message, id)
        fun systemSyncFailed(message: String = "System sync failed") = Error(ERROR_SYSTEM_SYNC_FAILED, message)
        fun accessTypeFailed(message: String = "Access type operation failed") = Error(ERROR_ACCESS_TYPE_FAILED, message)
        fun swapUnavailable(message: String = "Swap unavailable") = Error(ERROR_SWAP_UNAVAILABLE, message)
    }
}
