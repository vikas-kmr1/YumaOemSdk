package com.yumaoem.feature_home.data.dto.GetBatteryDetails

import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatteryDetailsResponseDto(
    @SerialName("item_group_id")
    val itemGroupId : Int?,

    @SerialName("qr_code")
    val qrCode : String,

    @SerialName("soc")
    val soc: Double? = null,

    @SerialName("soh")
    val soh: Double? = null,

    @SerialName("soe")
    val soe: Double? = null
)

fun BatteryDetailsResponseDto.toDomain() : BatteryDetails {
    return BatteryDetails(
        itemGroupId = itemGroupId,
        qrCode = qrCode,
        soc = soc,
        soh = soh,
        soe = soe
    )
}
