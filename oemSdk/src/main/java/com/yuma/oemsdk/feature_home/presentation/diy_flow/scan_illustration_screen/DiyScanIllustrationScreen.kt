package com.yumaoem.feature_home.presentation.diy_flow.scan_illustration_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography



@Composable
fun DiyScanIllustrationScreen(
    onNextClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        DiyScanIllustration(
            modifier = Modifier
                .padding(bottom = 80.dp)
                .align(Alignment.Center)
        )
        YumaPrimaryButton(
            buttonText = "Scan QR",
            onClick = onNextClicked,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding
                    (
                    horizontal = LocalDimensions.current.dimen20dp,
                    vertical = LocalDimensions.current.dimen40dp
                )
                .fillMaxWidth()
        )
    }
}

@Composable
fun DiyScanIllustration(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(all = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(LocalDimensions.current.dimen16dp))
            .background(Color(0xFFF5F7FA))
            .padding(
                vertical = LocalDimensions.current.dimen40dp,
                horizontal = 42.dp
            )
    ) {
        //YcuImage()
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "1. Scan the machine", style = LocalTypography.current.bodySemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "2. Swap battery", style = LocalTypography.current.bodySemiBold)
    }
}


@Preview
@Composable
fun IllustrationScreenPreview() {
    YumaAppTheme {
        DiyScanIllustrationScreen(
            onNextClicked = {}
        )
    }
}