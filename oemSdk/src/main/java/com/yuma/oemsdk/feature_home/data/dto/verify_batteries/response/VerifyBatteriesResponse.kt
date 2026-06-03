package com.yumaoem.feature_home.data.dto.verify_batteries.response

import com.yumaoem.feature_home.domain.model.token_flow.verify_batteries.VerifyBatteries
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyBatteriesResponse (
    @SerialName("statusCode")
    val status: Int,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: Boolean
)

fun VerifyBatteriesResponse.toDomain(): VerifyBatteries {
    return VerifyBatteries(
        data = data
    )
}