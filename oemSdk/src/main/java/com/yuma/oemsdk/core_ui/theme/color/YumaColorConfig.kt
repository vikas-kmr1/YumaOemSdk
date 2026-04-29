package com.yumaoem.core_ui.theme.color

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class Colors {
    TYPE_300,
    TYPE_400,
    TYPE_500,
    TYPE_600,
    TYPE_700,
    TYPE_800,
    TYPE_900,
}

fun yumaLightColors(
    primary: List<Color> = lightPrimaryColors,
    neutral: List<Color> = lightNeutralColors,
    red: List<Color> = lightRedColors,
    green: List<Color> = lightGreenColors,
    backgroundColor: Color = white
): YumaColors = YumaColors(
    primary = primary,
    neutral = neutral,
    red = red,
    green = green,
    backgroundColor = backgroundColor,
    isLight = true
)

fun yumaDarkColors(
    primary: List<Color> = lightPrimaryColors,
    neutral: List<Color> = lightNeutralColors,
    red: List<Color> = lightRedColors,
    green: List<Color> = lightGreenColors,
    backgroundColor: Color = black
): YumaColors = YumaColors(
    primary = primary,
    neutral = neutral,
    red = red,
    green = green,
    backgroundColor = backgroundColor,
    isLight = false
)

@Stable
class YumaColors(
    primary: List<Color>,
    neutral: List<Color>,
    red: List<Color>,
    green: List<Color>,
    backgroundColor: Color,
    isLight: Boolean
) {
    var primary by mutableStateOf(primary)
        private set

    var neutral by mutableStateOf(neutral)
        private set

    var red by mutableStateOf(red)
        private set

    var green by mutableStateOf(green)
        private set

    var backgroundColor by mutableStateOf(backgroundColor)
        private set

    var isLight by mutableStateOf(isLight)
        private set
}

val LocalColors = staticCompositionLocalOf { yumaLightColors() }
