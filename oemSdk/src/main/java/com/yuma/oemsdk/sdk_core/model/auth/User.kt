package com.yumaoem.core.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String,
    val phone: String,
    val firstName: String,
    val surname: String,
    val isPrePaidUser:Boolean,
    val clientId:Int,
    val clientCityId:Int,
    val clientUserId:Int,
    val clientVehicleQrCode:String,
    val clientVehicleId:Int,
    val clientVehicleGroupId:Int,
    val bikeProvider:String,
    val bikeNumber:String,
    val batteryCount:Int,
    val userProfileUrl: String? = null
)