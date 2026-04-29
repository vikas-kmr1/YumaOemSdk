package com.yumaoem.feature_home.domain.model.drop_off_data

import com.yumaoem.core.model.client.CoreClientVehicle
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails

data class DropOffScreenData(
    val screen: String,
    val isPrePaidUser: Boolean,
    val tokenDetails: BookedTokenDetails? = null,
    val vehicles: List<CoreClientVehicle>? = null,
    val batteryCount: Int
)