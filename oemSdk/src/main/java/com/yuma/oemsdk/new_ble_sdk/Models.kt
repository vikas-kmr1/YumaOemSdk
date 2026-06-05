package com.yumacustomer.new_ble_sdk.data


data class CommonSessionConfig(
    val userId: String, // customer user idd
    val qrCode: String, // ycu qr code
    val accessToken: String = "CE86D5F2F9F4DD46EEEE62FE4ABF9",
    val bikeName: String, // bike name
    val checkInTime: Long, // check in time in epoch
    val uuid: String = "cMLM08VkfG", // can send empty string no op
    val tokenId: Long, // booked token id
    val batteryCount: Int, // number of batteries in the bike
    val batteryType: Int, // 0 = gen-4, 1 = gen-5
    val swapType: Int = 1, // always 1
    val appVersion: String, // oem app version
    val applicationSourceId: String = "20", // application id for oem 12 is for yulu
    val currentLatitude: String,
    val currentLongitude: String,
    val clientCityId:Int,
    val clientVehicleId: Int,
    val isMultiYcuSwap: Boolean?,
    val partialCompletedCount: Int?,
)

data class CommonBatteryPortBin(
    val binNumber: String?,
    val port: Int?,
    val soc: String?
)

data class CommonSyncDifference(
    val pickedCommonBatteryPortBin: List<CommonBatteryPortBin>?,
    val droppedCommonBatteryPortBin: List<CommonBatteryPortBin>?,
    val initialBatteryBinInfo: List<CommonBatteryPortBin>?,
    val finalBatteryBinInfo: List<CommonBatteryPortBin>?,
    val droppedBatteryBinId: List<String>?
)

data class SmartSwapSubmitResponse(
    val id: Int,
    val message: String,
    val isManualFlowEnabled: Boolean,
    val timeTaken: String,
    val isTokenCompleted: Boolean,
    val isSessionTimedOut: Boolean,
    val isCallInitiated: Boolean,
    val isPingAvailable: Boolean,
    val isYcuScanAllowedAgain: Boolean,
)

data class SwapStatusResultDto(
    val swapStatus: Int,
    val updatedDt: Long,
    val isDoorOpen: Boolean,
    val slotId: Int
)

enum class SubmitType {
    SWAP_SUBMIT,
    MANUAL_SWAP_SUBMIT;
}

enum class DiySwapStatus(val id: Int, val message: String) {
    PING_NOT_RECEIVED(1, "Ping not received"),
    IOT_ATTEMPTS_FAILED(2, "IOT attempts failed"),
    TOKEN_ALREADY_FULFILLED(3, "Token already fulfilled"),
    SESSION_TIME_OUT(4, "Session time out"),
    DB_INSERT_BEFORE_TIMEOUT(5, "DB insert fail before timeout"),
    DB_INSERT_AFTER_TIMEOUT(6, "DB insert fail after timeout"),
    CB_DOOR_OPEN_FAILED(7, "CB Door Open Failed"),
    CB_NOT_REMOVED(8, "CB's not removed"),
    SWAP_NOT_COMPLETED(9, "Swap not completed"),
    SWAP_COMPLETED_SUCCESSFULLY(10, "Swap completed successfully"),
    SYSTEM_SYNC_EXCEPTION(100, "System sync failed during submit"),
    NO_SYNC_DIFFERENCE(101, "No sync difference found to submit"),
    BATTERY_COUNT_MISMATCH(102, "Picked and dropped battery count does not match expected"),
    NETWORK_FAILURE(103, "Network request failed during swap submit"),
    DOOR_CLOSED_WITH_BATTERY_2(104, "Close the door without inserting Discharged Battery 2");

}