package com.yumaoem.feature_home.presentation.profile_screen.components

import com.yumaoem.feature_home.domain.model.profile.swap_history.SwapsItem

sealed class UiSwapItem {
    data class DateHeader(val date: String) : UiSwapItem()
    data class SwapItem(val swap: SwapsItem) : UiSwapItem()
}
