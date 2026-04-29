package com.yumaoem.feature_home.presentation.home_screen.home_screen_host.bottom_nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors

private val NavigationBarHeight = 62.dp
internal val NavigationBarItemHorizontalPadding: Dp = 8.dp

@Composable
fun YumaNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        modifier = modifier
    ) {
        Column {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = LocalColors.current.neutral[Colors.TYPE_300.ordinal],
                thickness = 1.dp
            )
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .windowInsetsPadding(windowInsets)
                        .defaultMinSize(minHeight = NavigationBarHeight)
                        .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(NavigationBarItemHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }

    }
}