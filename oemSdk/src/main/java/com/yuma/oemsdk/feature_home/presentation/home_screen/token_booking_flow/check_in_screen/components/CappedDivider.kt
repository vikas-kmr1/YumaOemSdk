package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors

/**
 * A full‑width divider: two inward‑facing semicircles
 * joined by a dashed line.
 */
@Composable
fun InwardCappedDashedDivider(
    modifier: Modifier = Modifier,
    radiusDp: Float = 12f,
    dashDp: Float = 6f,
    gapDp: Float = 4f,
    strokeDp: Float = 2f,
    circleColor: Color = Color.White,
    lineColor:Color = LocalColors.current.neutral[Colors.TYPE_300.ordinal]
) {
    val density = LocalDensity.current
    fun Float.dp2px() = with(density) { this@dp2px.dp.toPx() }

    val rPx    = radiusDp.dp2px()
    val dashPx = dashDp.dp2px()
    val gapPx  = gapDp.dp2px()
    val stroke = strokeDp.dp2px()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height((radiusDp * 2).dp)          // tall enough for the caps
    ) {
        val centerY = size.height / 2f

        /* ---- 1️⃣  dashed rule (drawn first, gets masked by caps) ---- */
        drawLine(
            color       = lineColor,
            start       = Offset(2*rPx, centerY),                 // just after left cap’s flat edge
            end         = Offset(size.width - 2*rPx, centerY),    // just before right cap’s flat edge
            strokeWidth = stroke,
            pathEffect  = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx))
        )

        /* ---- 2️⃣  left cap: half the circle is outside the canvas ---- */
        drawArc(
            color       = circleColor,
            startAngle  = 270f,            // right half (curved side faces inwards)
            sweepAngle  = 180f,
            useCenter   = true,
            topLeft     = Offset(-rPx, centerY - rPx),           // shift ½ circle left
            size        = androidx.compose.ui.geometry.Size(rPx * 2, rPx * 2)
        )

        /* ---- 3️⃣  right cap: shift ½ circle to the right side ---- */
        drawArc(
            color       = circleColor,
            startAngle  = 90f,             // left half (curved side faces inwards)
            sweepAngle  = 180f,
            useCenter   = true,
            topLeft     = Offset(size.width - rPx, centerY - rPx),   // hang ½ outside right edge
            size        = androidx.compose.ui.geometry.Size(rPx * 2, rPx * 2)
        )
    }
}
