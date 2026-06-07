package com.yuma.oemsdk.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors

@Composable
fun SplashScreenRoot(){
    SplashScreen()
}


@Composable
private fun SplashScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColors.current.primary[Colors.TYPE_500.ordinal])
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(R.drawable.yuma_splash_logo),
                contentDescription = "Logo",
                contentScale = ContentScale.FillHeight,
            )
        }
    }
}


@Preview
@Composable
fun SplashScreenPreview() {
    YumaAppTheme {
        SplashScreen()
    }
}