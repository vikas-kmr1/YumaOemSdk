package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.components.circularGlow

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun GlowingCircle(
    radius: Dp = 100.dp,
    glowColor: Color = Color.Blue,
    coreColor: Color = Color.White,
    animationDuration: Int = 2000,
    modifier: Modifier = Modifier
) {
    // Animation for the pulsing glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "glow_transition")

    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_intensity"
    )

    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    Canvas(
        modifier = modifier.size(radius * 3) // Make canvas larger to accommodate glow
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radiusPx = radius.toPx()

        // Draw multiple layers for glow effect
        drawGlowLayers(
            centerX = centerX,
            centerY = centerY,
            radius = radiusPx,
            glowColor = glowColor,
            intensity = glowIntensity,
            scale = scaleAnimation
        )

        // Draw core circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    coreColor,
                    coreColor.copy(alpha = 0.8f),
                    glowColor.copy(alpha = 0.6f)
                )
            ),
            radius = radiusPx * 0.6f,
            center = Offset(centerX, centerY)
        )
    }
}

private fun DrawScope.drawGlowLayers(
    centerX: Float,
    centerY: Float,
    radius: Float,
    glowColor: Color,
    intensity: Float,
    scale: Float
) {
    val center = Offset(centerX, centerY)
    val glowLayers = listOf(
        Triple(radius * 2.2f * scale, glowColor.copy(alpha = 0.1f * intensity), 0.8f),
        Triple(radius * 1.8f * scale, glowColor.copy(alpha = 0.2f * intensity), 0.9f),
        Triple(radius * 1.4f * scale, glowColor.copy(alpha = 0.3f * intensity), 1f),
        Triple(radius * 1.1f * scale, glowColor.copy(alpha = 0.5f * intensity), 1f),
    )

    glowLayers.forEach { (layerRadius, color, layerScale) ->
        scale(layerScale, pivot = center) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color,
                        Color.Transparent
                    ),
                    radius = layerRadius
                ),
                radius = layerRadius,
                center = center
            )
        }
    }
}

@Preview
@Composable
fun GlowingCircleDemo() {
    var radius by remember { mutableStateOf(80f) }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val colors = listOf(
        Color.Blue to "Blue",
        Color.Red to "Red",
        Color.Green to "Green",
        Color.Magenta to "Magenta",
        Color.Cyan to "Cyan",
        Color.Yellow to "Yellow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main glowing circle
        GlowingCircle(
            radius = radius.dp,
            glowColor = colors[selectedColorIndex].first,
            modifier = Modifier.padding(32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Radius control
        Text(
            text = "Radius: ${radius.toInt()}dp",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )

        Slider(
            value = radius,
            onValueChange = { radius = it },
            valueRange = 30f..150f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Color selection
        Text(
            text = "Color: ${colors[selectedColorIndex].second}",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            colors.forEachIndexed { index, (color, _) ->
                GlowingCircle(
                    radius = 20.dp,
                    glowColor = color,
                    animationDuration = 1500,
                    modifier = Modifier
                        .padding(4.dp)
                        .let { mod ->
                            if (index == selectedColorIndex) {
                                mod.background(
                                    Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ).padding(4.dp)
                            } else mod
                        }
                        .clickable { selectedColorIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Multiple circles demonstration
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlowingCircle(
                radius = 40.dp,
                glowColor = Color.Red,
                animationDuration = 1000
            )
            GlowingCircle(
                radius = 50.dp,
                glowColor = Color.Green,
                animationDuration = 1500
            )
            GlowingCircle(
                radius = 35.dp,
                glowColor = Color.Blue,
                animationDuration = 2500
            )
        }
    }
}

@Composable
fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.pointerInput(Unit) {
            detectTapGestures { onClick() }
        }
    )
}


@Composable
fun GlowingCircleBackground(
    radius: Dp,
    glowColor: Color = Color.Blue,
    animationDuration: Int = 1000,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow_transition")

    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_intensity"
    )

    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    Canvas(
        modifier = modifier.size(radius * 2.5f) // Canvas size to accommodate glow
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radiusPx = radius.toPx()

        drawGlowLayers(
            centerX = centerX,
            centerY = centerY,
            radius = radiusPx,
            glowColor = glowColor,
            intensity = glowIntensity,
            scale = scaleAnimation
        )
    }
}