package com.yumaoem.feature_onboarding.data.dto.drop_off_screen.response

import com.yumaoem.core.model.client.ClientVehiclesItemDTO
import com.yumaoem.core.model.client.toCore
import com.yumaoem.feature_home.data.dto.book_token.response.BookTokenResponseDTO
import com.yumaoem.feature_home.domain.model.drop_off_data.DropOffScreenData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DropOffScreenResponseDTO(

    @SerialName("battery_count")
    val batteryCount: Int,

    @SerialName("clientVehicles")
    val clientVehicles: List<ClientVehiclesItemDTO>? = null,

    @SerialName("tokenDetails")
	val tokenDetails: BookTokenResponseDTO? = null,

    @SerialName("screen")
	val screen: String,

    @SerialName("is_prepaid_user")
	val isPrePaidUser: Boolean,
) {
    fun toDomain(): DropOffScreenData =
        DropOffScreenData(
            batteryCount = batteryCount,
            screen = screen,
            isPrePaidUser = isPrePaidUser,
            tokenDetails = tokenDetails?.toDomain(),
            vehicles = clientVehicles?.map { it.toCore() },
        )
}