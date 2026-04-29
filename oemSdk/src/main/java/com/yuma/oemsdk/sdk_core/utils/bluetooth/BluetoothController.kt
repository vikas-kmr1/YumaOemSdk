package com.yumaoem.core.utils.bluetooth

import android.content.Context
import com.yumaoem.core.utils.bluetooth.model.BluetoothDevice
import com.yumaoem.core.utils.data.AndroidBluetoothController
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDevice>>

    /**
     * for scanning beacons in IOS we need to pass the beaconUUIDString
     */
    fun startBeaconDiscovery(beaconUUIDString : String)

    fun startDiscovery()

    fun stopDiscovery()

    fun release()
}

/**
 * Platform-specific factory function.
 * On Android it will return your AndroidBluetoothController;
 * on iOS it will return IOSBluetoothController
 */
fun getPlatformBluetoothController(
    androidContext:Any?
): BluetoothController = AndroidBluetoothController(androidContext as Context)
