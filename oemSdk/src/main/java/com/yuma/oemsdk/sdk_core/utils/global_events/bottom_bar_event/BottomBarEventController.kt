package com.yumaoem.core.utils.global_events.bottom_bar_event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object BottomBarEventController {

    private val _events = Channel<Any>(Channel.BUFFERED)
    val events: Flow<Any> = _events.receiveAsFlow()

    suspend fun <T : Any> sendEvent(event: T) {
        _events.send(event)
    }
}
