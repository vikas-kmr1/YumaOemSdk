package com.yumaoem.core_ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.dimension.YumaDimensions
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.color.YumaColors
import com.yumaoem.core_ui.theme.color.yumaDarkColors
import com.yumaoem.core_ui.theme.shapes.ExtendedShapes
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.shapes.appShapes
import com.yumaoem.core_ui.theme.typography.YumaTypography
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.core_ui.theme.typography.Type

object YumaAppTheme {
    val colors: YumaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val dimensions: YumaDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current

    val shapes: ExtendedShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current
}

@Composable
fun YumaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    lightColors: YumaColors = YumaAppTheme.colors,
    darkColors: YumaColors = yumaDarkColors(),
    typography: YumaTypography = Type.typography(),
    space: YumaDimensions = YumaAppTheme.dimensions,
    shapes: ExtendedShapes = YumaAppTheme.shapes,
    content: @Composable () -> Unit
) {
    val currentColorSet = remember { if (darkTheme) darkColors else lightColors }
    CompositionLocalProvider(
        LocalColors provides currentColorSet,
        LocalTypography provides typography,
        LocalDimensions provides space,
        LocalAppShapes provides appShapes
    ) {
        content()
    }
}
