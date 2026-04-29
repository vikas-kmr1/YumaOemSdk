package com.yumaoem.core_ui.theme.dimension

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class YumaDimensions(
    val dimen0dp: Dp = 0.dp,
    val dimenQuaterDp : Dp = 0.25.dp,
    val dimenHalfDp: Dp = 0.5.dp,
    val dimen1dp: Dp = 1.dp,
    val dimen2dp: Dp = 2.dp,
    val dimen3dp: Dp = 3.dp,
    val dimen4dp: Dp = 4.dp,
    val dimen5dp: Dp = 5.dp,
    val dimen6dp: Dp = 6.dp,
    val dimen7dp: Dp = 7.dp,
    val dimen8dp: Dp = 8.dp,
    val dimen9dp: Dp = 9.dp,
    val dimen10dp: Dp = 10.dp,
    val dimen11dp: Dp = 11.dp,
    val dimen12dp: Dp = 12.dp,
    val dimen14dp: Dp = 14.dp,
    val dimen16dp: Dp = 16.dp,
    val dimen18dp: Dp = 18.dp,
    val dimen20dp: Dp = 20.dp,
    val dimen22dp: Dp = 22.dp,
    val dimen24dp: Dp = 24.dp,
    val dimen26dp: Dp = 26.dp,
    val dimen28dp: Dp = 28.dp,
    val dimen30dp: Dp = 30.dp,
    val dimen32dp: Dp = 32.dp,
    val dimen40dp: Dp = 40.dp,
    val dimen48dp: Dp = 48.dp,
    val dimen50dp: Dp = 50.dp,
    val dimen56dp: Dp = 56.dp,
    val dimen64dp :Dp = 64.dp,
    val dimen80dp: Dp = 80.dp,
    val dimen90dp: Dp = 90.dp,
    val dimen100dp: Dp = 100.dp,
    val dimen110dp: Dp = 110.dp,
    val dimen120dp : Dp = 120.dp,
    val dimen160dp: Dp = 160.dp,
    val dimen360dp: Dp = 360.dp,
    val yumaCardCornerRadius: Dp = 24.dp,
    val yumaTextFieldRadius: Dp = 16.dp
)

val LocalDimensions = staticCompositionLocalOf { YumaDimensions() }