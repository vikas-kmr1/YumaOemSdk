package com.yumaoem.core.utils.data.mapper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.yumaoem.core.utils.bluetooth.model.BluetoothDeviceDomain


@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}