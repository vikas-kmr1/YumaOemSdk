package com.yumaoem.feature_home.domain.usecase.maps.station_operation_status

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime

class GetCurrentDayOfWeekUseCase {
    operator fun invoke(): Int {
        val now = Clock.System.now()
        val currentDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return currentDateTime.date.dayOfWeek.isoDayNumber
    }
}
