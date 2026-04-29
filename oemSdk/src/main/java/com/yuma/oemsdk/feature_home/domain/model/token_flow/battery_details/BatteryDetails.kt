package com.yumaoem.feature_home.domain.model.token_flow.battery_details

data class BatteryDetails(
    val itemGroupId: Int?,
    val qrCode: String,
    val soc : Double? = null,
    val soh : Double? = null,
    val soe : Double? = null,
)
