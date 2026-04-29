package com.yumaoem.feature_home.domain.model.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuma.oemsdk.R
import kotlinx.serialization.Serializable


data class PaymentPlansUiModel(
    val header: PaymentHomeUiHeader,
    val currentPlanDetails: UiCurrentPlan?,
    val plansSectionHeading: UiText,
    val allPlans: List<UiPlanGroup>,
    val footerNotes: List<String>,
    val canBuyPlan: Boolean
)

data class PaymentHomeUiHeader(
    val title: UiText,
    val state: UiText
)

data class UiCurrentPlan(
    val swapsStatus: List<UiText>,
    val swapsLabel: UiText,
    val expiry: UiText
)

data class UiPlanGroup(
    val groupName: String,
    val plans: List<UiPlan>
)

@Serializable
data class UiPlan(
    val id: String,
    val title: String,
    val range: String,
    val validity: String,
    val totalAmount:String,
    val amountWithoutTax: String,
    val taxAmount: String,
    val isSelected:Boolean = false
)

// A reusable text style class that can render any styled label
data class UiText(
    val text: String,
    val textColor: String?,
    val backgroundColor: String? = null,
    val fontSize: Int? = null,
    val fontWeight: Int? = null,
    val borderColor: String? = null
)

@Composable
fun UiText.toTextStyle(
    default: TextStyle = TextStyle.Default
): TextStyle {
    return default.copy(
        fontFamily = FontFamily(Font(R.font.poppins_regular, weight = FontWeight.Normal)),
        color = parseColorOrDefault(textColor, default.color),
        fontSize = (fontSize ?: default.fontSize.value.toInt()).sp,
        fontWeight = fontWeight?.let { FontWeight(it) } ?: default.fontWeight
    )
}

fun parseColorOrDefault(hex: String?, fallback: Color = Color.Unspecified): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorLong = when (cleanHex.length) {
            6 -> ("FF$cleanHex").toLong(16)
            8 -> cleanHex.toLong(16)
            else -> return fallback
        }
        Color(colorLong)
    } catch (_: Exception) {
        fallback
    }
}

fun UiText.toModifier(
    default: Modifier = Modifier
): Modifier {
    val bgColor = parseColorOrDefault(backgroundColor, Color.Transparent)
    val borderClr = parseColorOrDefault(borderColor, Color.Transparent)

    return default
        .background(bgColor)
        .border(
            width = 1.dp, color = borderClr,
            shape = RoundedCornerShape(16.dp)
        )
}
