package com.yuma.oemsdk.feature_home.presentation.diy_flow.scan_qr

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun DrawScope.drawQRScannerOverlay(bottomSpace: Dp = 120.dp) {
    val canvasWidth = size.width
    val canvasHeight = size.height

    // Calculate square viewfinder size and position
    val viewfinderSize = minOf(canvasWidth, canvasHeight) * 0.6f
    val viewfinderLeft = (canvasWidth - viewfinderSize) / 2f
    val viewfinderTop = (canvasHeight - viewfinderSize) / 2f - bottomSpace.toPx() // Slightly higher to make room for text field

    // IMPORTANT: Draw overlay first, then clear the viewfinder area

    // Draw semi-transparent overlay over entire screen
    drawRect(
        color = Color(0x88000000),
        size = Size(canvasWidth, canvasHeight)
    )

    // Draw corner brackets
    val cornerLength = 40.dp.toPx()
    val cornerThickness = 4.dp.toPx()
    val cornerColor = Color(0xFF4A90E2)

    // Top-left corner
    drawLine(
        color = cornerColor,
        start = Offset(viewfinderLeft, viewfinderTop),
        end = Offset(viewfinderLeft + cornerLength, viewfinderTop),
        strokeWidth = cornerThickness,
        cap = StrokeCap.Round
    )
    drawLine(
        color = cornerColor,
        start = Offset(viewfinderLeft, viewfinderTop),
        end = Offset(viewfinderLeft, viewfinderTop + cornerLength),
        strokeWidth = cornerThickness,
        cap = StrokeCap.Round
    )

    // Top-right corner
    drawLine(
        color = cornerColor,
        start = Offset(viewfinderLeft + viewfinderSize - cornerLength, viewfinderTop),
        end = Offset(viewfinderLeft + viewfinderSize, viewfinderTop),
        strokeWidth = cornerThickness,
        cap = StrokeCap.Round
    )
    drawLine(
        color = cornerColor,
        start = Offset(viewfinderLeft + viewfinderSize, viewfinderTop),
        end = Offset(viewfinderLeft + viewfinderSize, viewfinderTop + cornerLength),
        strokeWidth = cornerThickness,
        cap = StrokeCap.Round
    )

    // Bottom-left corner
    drawLine(
        color = cornerColor,
        start = Offset(viewfinderLeft, viewfinderTop + viewfinderSize - cornerLength),
        end = Offset(viewfinderLeft, viewfinderTop + viewfinderSize),
        strokeWidth = cornerThickness,
        cap = StrokeCap.Round
    )
    drawLine(
        color = cornerColor,
        start = Offset(viewfinderLeft, viewfinderTop + viewfinderSize),
        end = Offset(viewfinderLeft + cornerLength, viewfinderTop + viewfinderSize),
        strokeWidth = cornerThickness,
        cap = StrokeCap.Round
    )

    // Bottom-right corner
    drawLine(
        color = cornerColor,
        start = Offset(viewfinderLeft + viewfinderSize, viewfinderTop + viewfinderSize - cornerLength),
        end = Offset(viewfinderLeft + viewfinderSize, viewfinderTop + viewfinderSize),
        strokeWidth = cornerThickness,
        cap = StrokeCap.Round
    )
    drawLine(
        color = cornerColor,
        start = Offset(viewfinderLeft + viewfinderSize - cornerLength, viewfinderTop + viewfinderSize),
        end = Offset(viewfinderLeft + viewfinderSize, viewfinderTop + viewfinderSize),
        strokeWidth = cornerThickness,
        cap = StrokeCap.Round
    )
}