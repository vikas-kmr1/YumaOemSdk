package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_success_screen.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yuma.oemsdk.R
import com.yumaoem.core.utils.global_events.HideBottomBar
import com.yumaoem.core.utils.global_events.ShowBottomBar
import com.yumaoem.core.utils.global_events.bottom_bar_event.BottomBarEventController
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography
import kotlinx.coroutines.delay


@Composable
fun SwapSuccessScreen(
    swapTime: String,
    navigateToHomeScreen: () -> Unit
) {

    LaunchedEffect(Unit) {
        BottomBarEventController.sendEvent(HideBottomBar)
    }


    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.success)
    )

    var playing by remember { mutableStateOf(true) }

    val animationState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = playing
    )

    LaunchedEffect(Unit) {
        Unit
    }
    LaunchedEffect(animationState.progress) {
        if (
            animationState.progress == 1f) {
            delay(2000)
            BottomBarEventController.sendEvent(ShowBottomBar)
            navigateToHomeScreen()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(LocalColors.current.green[Colors.TYPE_500.ordinal]),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            Spacer(modifier = Modifier.height(120.dp))
            Text(
                text = stringResource(R.string.you_got_your_battery_in),
                color = LocalColors.current.green[Colors.TYPE_300.ordinal],
                style = LocalTypography.current.heading5
            )
            Text(
                text = swapTime,
                color = Color.White,
                style = LocalTypography.current.heading2SemiBold
            )
        }
        LottieAnimation(
            composition = composition,
            progress = { animationState.progress },
            modifier = Modifier
                .padding(40.dp)
                .align(Alignment.Center),
        )

    }
}

@Preview
@Composable
fun SwapSuccessScreenPreview() {
    YumaAppTheme {
        SwapSuccessScreen(
            swapTime = "2h 30m",
            navigateToHomeScreen = {}
        )
    }
}