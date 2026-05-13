package com.yumaoem.feature_home.domain.usecase.maps.station_operation_status

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class GetCurrentDayOfWeekUseCase {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(): Int {
        val currentDateTime = LocalDate.now()
        return currentDateTime.dayOfWeek.value
    }
}
