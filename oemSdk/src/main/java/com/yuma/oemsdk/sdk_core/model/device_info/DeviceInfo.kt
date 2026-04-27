package com.yumaoem.core.model.device_info

data class DeviceInfo(
    val model: String,
    val manufacturer: String,
    val osName: String,
    val osVersion: String,
    val appVersion: String,
    val locale: String,
    val timezone: String,
    val screenResolution: String,
    val deviceType: String,
    val androidId: String
)