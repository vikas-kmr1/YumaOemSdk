package com.yumaoem.core.utils.data

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import androidx.annotation.RequiresPermission
import com.yumaoem.core.utils.bluetooth.BluetoothController
import com.yumaoem.core.utils.bluetooth.model.BluetoothDeviceDomain
import com.yumaoem.core.utils.data.FoundDeviceReceiver.FoundDeviceReceiver
import com.yumaoem.core.utils.data.mapper.toBluetoothDeviceDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AndroidBluetoothController(
    private val context: Context
): BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())

    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices



    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startBeaconDiscovery(beaconUUIDString: String) {
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        bluetoothAdapter?.startDiscovery()
    }

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if(newDevice in devices) devices else devices + newDevice
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startDiscovery() {
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        bluetoothAdapter?.startDiscovery()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }
}