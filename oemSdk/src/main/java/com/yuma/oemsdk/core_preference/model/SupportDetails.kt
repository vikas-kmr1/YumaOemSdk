package com.yumaoem.corepreference.model
import kotlinx.serialization.Serializable

typealias PrefSupportDetails = SupportDetails

@Serializable
data class SupportDetails(
    val phoneNumber: Long,
    val defaultMessage:String,
)
