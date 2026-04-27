package com.yumaoem.core.utils.bluetooth.model

typealias BluetoothDeviceDomain = BluetoothDevice

data class BluetoothDevice(
    val name:String?,
    val address:String,
    val major: Int = 0,
    val minor: Int = 0,
)
