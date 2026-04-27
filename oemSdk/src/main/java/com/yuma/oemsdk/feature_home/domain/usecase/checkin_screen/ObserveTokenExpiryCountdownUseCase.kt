package com.yumaoem.feature_home.domain.usecase.checkin_screen

import com.yumaoem.core.utils.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object ObserveTokenExpiryCountdownUseCase {
    operator fun invoke(tokenExpiryTimestamp: Long): Flow<String> = flow {
        val expiryMillis = tokenExpiryTimestamp * 1000L
        while (true) {
            val currentTime = currentTimeMillis()
            val millisLeft = expiryMillis - currentTime

            if (millisLeft <= 0L) {
                emit("00:00")
                break
            }

            val minutes = (millisLeft / 1000) / 60
            val seconds = (millisLeft / 1000) % 60
            val formatted = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
            emit(formatted)
            delay(1000L)
        }
    }.flowOn(Dispatchers.Default)
}
