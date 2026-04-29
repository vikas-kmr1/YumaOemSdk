package com.yumaoem.core.utils.bluetooth

import android.bluetooth.BluetoothAdapter

/** Returns true if Bluetooth is currently powered on. */
fun isBluetoothEnabled(): Boolean {
    val adapter = BluetoothAdapter.getDefaultAdapter()
    return adapter?.isEnabled == true
}