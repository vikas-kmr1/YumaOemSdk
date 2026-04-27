package com.yumaoem.feature_home.presentation.home_screen.tag_battery

sealed interface DiyScanBatteryIntent {

    object OnFlashLightClicked : DiyScanBatteryIntent

    data class OnScanCompleted(
        val scannedCode: String,
        val isManualEntry: Boolean = false
    ) : DiyScanBatteryIntent

    object RetryScan : DiyScanBatteryIntent

    object OnScreenViewed : DiyScanBatteryIntent
}
