package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_on_break_with_timer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.closed_station.ClosedStationStateChip
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_info_bottom_sheet_common_components.GoogleMapLogoIcon
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StationOnBreakWithTimerCard(
    stationName: String,
    stationOpeningTimeStamp: Int,
    onDirectionsClicked: () -> Unit,
    onBreakFinished: () -> Unit
) {
    val remainingSeconds by countdownToNextOpening(stationOpeningTimeStamp)
        .collectAsStateWithLifecycle(initialValue = -1)

    LaunchedEffect(remainingSeconds){
        if (remainingSeconds == 0){
            onBreakFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(LocalAppShapes.current.chipShape)
            .background(LocalColors.current.red[Colors.TYPE_300.ordinal])
            .padding(all = LocalDimensions.current.dimen20dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stationName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = LocalTypography.current.bodyLargeSemiBold
                )
                Spacer(Modifier.height(LocalDimensions.current.dimen12dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TimerChip(timeLeft = remainingSeconds.toString())
                }
            }
            Spacer(Modifier.width(LocalDimensions.current.dimen6dp))
            GoogleMapLogoIcon(onDirectionsClicked = onDirectionsClicked)
        }
    }
}

@Composable
fun TimerChip(
    timeLeft: String
) {
    Box(
        modifier = Modifier
            .clip(LocalAppShapes.current.chipShape)
            .background(
                color = Color.White
            )
            .wrapContentWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ClosedStationStateChip(
                "Break time",
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.width(60.dp),
                text = "$timeLeft sec",
                style = LocalTypography.current.smallBodySemiBold.copy(
                    color = LocalColors.current.red[Colors.TYPE_500.ordinal],
                )
            )
        }
    }
}
/**
 * Flow that emits the remaining seconds until [nextOpeningTime]
 * (seconds since midnight in the device’s current time-zone).
 *
 * Starts with the *current* difference and counts down every second.
 * Completes when the difference reaches 0 or becomes negative.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun countdownToNextOpening(nextOpeningTime: Int?): Flow<Int> = flow {
    if (nextOpeningTime == null) {
        emit(0)
        return@flow
    }

    while (currentCoroutineContext().isActive) {
        val remaining = remainingSeconds(nextOpeningTime)
        emit(remaining.coerceAtLeast(0))

        if (remaining <= 0) break
        delay(1_000)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun remainingSeconds(nextOpeningTime: Int): Int {
    val local = LocalDateTime.now()

    val nowSeconds = local.hour * 3_600 + local.minute * 60 + local.second
    return nextOpeningTime - nowSeconds
}

