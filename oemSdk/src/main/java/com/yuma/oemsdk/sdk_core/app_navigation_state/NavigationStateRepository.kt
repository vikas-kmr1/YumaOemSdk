package com.yumaoem.core.app_navigation_state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationStateRepository() {
    private val _currentHomeDestination =
        MutableStateFlow(HomeScreenDestination.MapScreen)
    val currentHomeDestination: StateFlow<HomeScreenDestination> =
        _currentHomeDestination.asStateFlow()

    var currentBatterySwap: Int = 1
    var isMultiYcuFlow: Boolean = false

    fun resetMultiYcuState() {
        currentBatterySwap = 1
        isMultiYcuFlow = false
    }

    fun updateHomeDestination(destination: HomeScreenDestination) {
        if (_currentHomeDestination.value != destination) {
            _currentHomeDestination.value = destination
        }
    }

    fun resetToMapScreen() {
        _currentHomeDestination.value = HomeScreenDestination.MapScreen
    }
}
