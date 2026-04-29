package com.yumaoem.feature_home.domain.model.profile.swap_history

import androidx.annotation.DrawableRes

data class SwapsItem(
	val serviceTime: String,
	val swapTime: String,
	@DrawableRes val icon: Int?,
)

data class SwapHistoryItem(
	val date: String,
	val swaps: List<SwapsItem>
)