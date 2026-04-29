package com.yumaoem.feature_home.domain.model.profile

data class UserDetails(
    val fullName: String,
    val mobileNumber: String,
    val profileImageUrl: String,
    val bikeProvider: String,
    val bikeNumber: String,
    val bikeQrNumber: String,
)