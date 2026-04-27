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
    val applicationSourceId: String = "12", // application id for oem 12 is for yulu
    val currentLatitude: String,
    val currentLongitude: String,
    val clientCityId:Int,
    val clientVehicleId: Int
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