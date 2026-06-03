package com.yumaoem.feature_home.presentation.payments.payment_success


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yuma.oemsdk.R
import com.yuma.oemsdk.YumaSdk
import com.yumaoem.core.utils.resource_reader.FileReader
import com.yumaoem.core.utils.sound_player.SoundPlayer
import com.yumaoem.core.utils.vibration.vibrate

import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.payments.plan_selection.components.SolidCircle3dp
import kotlinx.coroutines.delay
@androidx.annotation.RequiresPermission(android.Manifest.permission.VIBRATE)
@Composable
fun PaymentSuccessScreenRoot(
    navigateToHomeTab: () -> Unit
) {
    val fileReader: FileReader = remember { FileReader() }

    val successSound: ByteArray? by remember {
        mutableStateOf(fileReader.readBytes("celebration.mp3"))
    }

    val audioPlayer: SoundPlayer = YumaSdk.soundPlayer

    LaunchedEffect(Unit) {
        delay(200)
        successSound?.let {
            audioPlayer.playSound(it, onComplete = {
                audioPlayer.release()
            },
                onError = {
                    audioPlayer.release()
                },
                format = "")
        }
        vibrate(200)
    }

    PaymentSuccessScreen(
        numberOfSwaps = "1 Swaps",
        days = "7 Days",
        navigateToHomeTab = navigateToHomeTab
    )
}


@Composable
fun PaymentSuccessScreen(
    numberOfSwaps:String,
    days:String,
    navigateToHomeTab: () -> Unit
) {

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.success)
    )
    var playing by remember { mutableStateOf(true) }
    val animationState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = playing
    )



    Box(
        modifier = Modifier
            .padding(top = 28.dp)
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                modifier = Modifier
                    .size(120.dp),
                progress = { animationState.progress }
            )
            Text(
                text = "Payment Successful!",
                style = LocalTypography.current.bodyLargeMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            ElevatedCard(
                onClick = {},
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier
                    .wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = numberOfSwaps,
                        style = LocalTypography.current.smallBodyMedium
                    )
                    Spacer(Modifier.width(8.dp))
                    SolidCircle3dp(
                        color = Color.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = days,
                        style = LocalTypography.current.smallBodyMedium
                    )
                }
            }
            Spacer(Modifier.height(36.dp))
            Text(
                textAlign = TextAlign.Center,
                text = "Your battery plan will activate at the\ntime of your first swap!",
                style = LocalTypography.current.smallBodyMedium.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_600.ordinal]
                )
            )
            Spacer(Modifier.height(56.dp))
        }
        YumaPrimaryButton(
            enabled = true,
            modifier = Modifier
                .padding(horizontal = LocalDimensions.current.dimen20dp, vertical = LocalDimensions.current.dimen24dp)
                .align(Alignment.BottomCenter),
            buttonText = "Book Battery",
            onClick = navigateToHomeTab
        )
    }
}

@Preview
@Composable
fun PaymentSuccessScreenPreview() {
    YumaAppTheme {
        PaymentSuccessScreen(
            numberOfSwaps = "1 Swaps",
            days = "7 Days",
            navigateToHomeTab = {}
        )
    }
}