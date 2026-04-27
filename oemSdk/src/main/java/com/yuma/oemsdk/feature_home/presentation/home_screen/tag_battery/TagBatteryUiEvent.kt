package com.yumaoem.feature_home.presentation.home_screen.tag_battery

sealed class TagBatteryUiEvent {
    data class ShowError(val message: String) : TagBatteryUiEvent()
    data object NavigateToMapScreen: TagBatteryUiEvent()

    data class ShowSuccessSnackbar(val message: String) : TagBatteryUiEvent()
}
