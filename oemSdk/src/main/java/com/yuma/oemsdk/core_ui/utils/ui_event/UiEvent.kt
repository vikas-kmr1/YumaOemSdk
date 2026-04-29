package com.yumaoem.core_ui.utils.ui_event


sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
}
