package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R

import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.components.circularGlow.GlowingCircleBackground

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun TokenDetailsView(
    tokenNumber: String,
    tokenExpiryTime: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TokenNumberView(
            tokenNumber = tokenNumber,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
        TokenExpiryTimerView(
            timeLeft = tokenExpiryTime,
            modifier = Modifier
        )
    }
}

@Composable
private fun TokenExpiryTimerView(
    timeLeft: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val paddingDp by animateDpAsState(
        targetValue = if (expanded) 12.dp else 8.dp,
        animationSpec = tween(durationMillis = 800)
    )

    //Setting up an infinite transition for rotating our gradient
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    BoxWithConstraints(modifier = modifier) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val heightPx = with(LocalDensity.current) { maxHeight.toPx() }
        val center = Offset(x = widthPx / 2f, y = heightPx / 2f)
        val radius = hypot(widthPx, heightPx) / 2f
        val angleRad = rotation.toDouble() * PI / 180.0
        val dx = cos(angleRad).toFloat() * radius
        val dy = sin(angleRad).toFloat() * radius
        val start = center + Offset(dx, dy)
        val end = center - Offset(dx, dy)
        val borderBrush = if (!expanded) {
            SolidColor(LocalColors.current.neutral[Colors.TYPE_900.ordinal])
        } else {
            Brush.linearGradient(
                colors = listOf(
                    LocalColors.current.primary[Colors.TYPE_700.ordinal],
                    LocalColors.current.red[Colors.TYPE_500.ordinal],
                    LocalColors.current.green[Colors.TYPE_400.ordinal]
                ),
                start = start,
                end = end,
                tileMode = TileMode.Mirror
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(170.dp)
                .border(
                    width = 2.dp,
                    brush = borderBrush,
                    shape = RoundedCornerShape(LocalDimensions.current.dimen12dp)
                )
                .padding(paddingDp)
        ) {
            Text(
                text = "${stringResource(R.string.expires_in)} $timeLeft",
                style = LocalTypography.current.body,
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun TokenNumberView(
    tokenNumber: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(ratio = 1f,matchHeightConstraintsFirst = true)       // …keep it square but let parent decide size
            .clip(CircleShape)
            .background(Color.White)
            .padding(LocalDimensions.current.dimen10dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.token),
                style = LocalTypography.current.body,
                color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                textAlign = TextAlign.Center
            )
            Text(
                text = tokenNumber.toString(),
                style = LocalTypography.current.heading2,
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TokenNumberViewWithGlow(
    tokenNumber: String,
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF64B5F6),
    showGlow: Boolean = true
) {
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = modifier
    ) {
        val foregroundRadius = with(density) {
            minOf(maxWidth, maxHeight) / 2
        }
        val glowRadius = foregroundRadius + 16.dp

        Box(
            contentAlignment = Alignment.Center
        ) {
            // Glowing background circle
            if (showGlow) {
                GlowingCircleBackground(
                    radius = glowRadius,
                    glowColor = glowColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Foreground circle with content
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = true)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(LocalDimensions.current.dimen10dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.token),
                        style = LocalTypography.current.body,
                        color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = tokenNumber,
                        style = LocalTypography.current.heading2,
                        color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TokenDetailsViewPreview() {
    YumaAppTheme {
        TokenDetailsView(
            tokenNumber = "12",
            tokenExpiryTime = "00:00"
        )
    }
}
