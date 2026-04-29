package com.yumaoem.core_ui.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.yuma.oemsdk.R

/** Custom typography for Yuma App */
object Type {
    /**
     * Provides the custom typography styles.
     *
     * Each text style is defined with a specific font family, weight, size, and line height.
     *
     * @return The custom typography instance.
     */
    @Composable
    fun typography(): YumaTypography {
        val poppinsRegular = FontFamily(Font(R.font.poppins_regular, weight = FontWeight.Normal))
        val poppinsMedium = FontFamily(Font(R.font.poppins_medium, weight = FontWeight.Medium))
        val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold, weight = FontWeight.SemiBold))

        return YumaTypography(
            heading1 = TextStyle(fontFamily = poppinsRegular, fontWeight = FontWeight.Normal, fontSize = 48.sp, lineHeight = 72.sp),
            heading1Medium = TextStyle(fontFamily = poppinsMedium, fontWeight = FontWeight.Medium, fontSize = 48.sp, lineHeight = 72.sp),
            heading1SemiBold = TextStyle(fontFamily = poppinsSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 48.sp, lineHeight = 72.sp),
            heading2 = TextStyle(fontFamily = poppinsRegular, fontWeight = FontWeight.Normal, fontSize = 40.sp, lineHeight = 60.sp),
            heading2Medium = TextStyle(fontFamily = poppinsMedium, fontWeight = FontWeight.Medium, fontSize = 40.sp, lineHeight = 60.sp),
            heading2SemiBold = TextStyle(fontFamily = poppinsSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 40.sp, lineHeight = 60.sp),
            heading3 = TextStyle(fontFamily = poppinsRegular, fontWeight = FontWeight.Normal, fontSize = 32.sp, lineHeight = 48.sp),
            heading3Medium = TextStyle(fontFamily = poppinsMedium, fontWeight = FontWeight.Medium, fontSize = 32.sp, lineHeight = 48.sp),
            heading3SemiBold = TextStyle(fontFamily = poppinsSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 48.sp),
            heading4 = TextStyle(fontFamily = poppinsRegular, fontWeight = FontWeight.Normal, fontSize = 28.sp, lineHeight = 42.sp),
            heading4Medium = TextStyle(fontFamily = poppinsMedium, fontWeight = FontWeight.Medium, fontSize = 28.sp, lineHeight = 42.sp),
            heading4SemiBold = TextStyle(fontFamily = poppinsSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 42.sp),
            heading5 = TextStyle(fontFamily = poppinsRegular, fontWeight = FontWeight.Normal, fontSize = 24.sp, lineHeight = 36.sp),
            heading5Medium = TextStyle(fontFamily = poppinsMedium, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 36.sp),
            heading5SemiBold = TextStyle(fontFamily = poppinsSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 36.sp),
            bodyLarge = TextStyle(fontFamily = poppinsRegular, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 30.sp),
            bodyLargeMedium = TextStyle(fontFamily = poppinsMedium, fontWeight = FontWeight.Medium, fontSize = 20.sp, lineHeight = 30.sp, textAlign = TextAlign.Center),
            bodyLargeSemiBold = TextStyle(fontFamily = poppinsSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 30.sp),
            body = TextStyle(fontFamily = poppinsRegular, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
            bodyMedium = TextStyle(fontFamily = poppinsMedium, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
            bodySemiBold = TextStyle(fontFamily = poppinsSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
            smallBody = TextStyle(fontFamily = poppinsRegular, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
            smallBodyMedium = TextStyle(fontFamily = poppinsMedium, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 21.sp),
            smallBodySemiBold = TextStyle(fontFamily = poppinsSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 21.sp)
        )
    }
}

@Immutable
class YumaTypography(
    val heading1: TextStyle,
    val heading1Medium: TextStyle,
    val heading1SemiBold: TextStyle,
    val heading2: TextStyle,
    val heading2Medium: TextStyle,
    val heading2SemiBold: TextStyle,
    val heading3: TextStyle,
    val heading3Medium: TextStyle,
    val heading3SemiBold: TextStyle,
    val heading4: TextStyle,
    val heading4Medium: TextStyle,
    val heading4SemiBold: TextStyle,
    val heading5: TextStyle,
    val heading5Medium: TextStyle,
    val heading5SemiBold: TextStyle,
    val bodyLarge: TextStyle,
    val bodyLargeMedium: TextStyle,
    val bodyLargeSemiBold: TextStyle,
    val body: TextStyle,
    val bodyMedium: TextStyle,
    val bodySemiBold: TextStyle,
    val smallBody: TextStyle,
    val smallBodyMedium: TextStyle,
    val smallBodySemiBold: TextStyle
)

val LocalTypography = staticCompositionLocalOf<YumaTypography> {
    error("No Typography provided")
}


//
///** Object that holds the custom typography for the Compose theme. */
//object Type {
//    /**
//     * Provides the custom [Typography] for the Compose theme.
//     *
//     * The [Typography] object contains different text styles for various text elements in the theme.
//     * Each text style is defined with a specific [FontFamily], [FontWeight], and [fontSize].
//     *
//     * @return The custom [Typography] for the theme.
//     */
//    val typography: Typography
//        @Composable
//        get() {
//            return CustomTypography()
//        }
//
//    /**
//     * Composable function that creates and returns the custom [Typography].
//     *
//     * @return The custom [Typography] instance.
//     */
//    @Composable
//    private fun CustomTypography(): Typography {
//        val poppinsRegular =
//            FontFamily(Font(Res.font.Poppins_Regular, weight = FontWeight.Normal))
//        val poppinsMedium =
//            FontFamily(Font(Res.font.Poppins_SemiBold, weight = FontWeight.SemiBold))
//        val poppinsSemiBold =
//            FontFamily(Font(Res.font.Poppins_Medium, weight = FontWeight.Bold))
//
//        val typo = Typography(
//            displayLarge = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 32.sp,
//            ),
//            displayMedium = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 24.sp,
//            ),
//            displaySmall = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 20.sp,
//            ),
//            headlineLarge = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 16.sp,
//            ),
//            headlineMedium = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 14.sp,
//            ),
//            headlineSmall = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 12.sp,
//            ),
//            titleLarge = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 10.sp,
//            ),
//            titleMedium = TextStyle(
//                fontFamily = poppinsRegular,
//                fontWeight = FontWeight.Normal,
//                fontSize = 12.sp,
//            ),
//            titleSmall = TextStyle(
//                fontFamily = poppinsRegular,
//                fontWeight = FontWeight.Normal,
//                fontSize = 14.sp,
//            ),
//            bodyLarge = TextStyle(
//                fontFamily = poppinsRegular,
//                fontWeight = FontWeight.Normal,
//                fontSize = 12.sp,
//            ),
//            bodyMedium = TextStyle(
//                fontFamily = poppinsRegular,
//                fontWeight = FontWeight.Normal,
//                fontSize = 14.sp,
//            ),
//            bodySmall = TextStyle(
//                fontFamily = poppinsRegular,
//                fontWeight = FontWeight.Normal,
//                fontSize = 12.sp,
//            ),
//            labelLarge = TextStyle(
//                fontFamily = poppinsRegular,
//                fontWeight = FontWeight.Bold,
//                fontSize = 14.sp,
//            ),
//            labelMedium = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 12.sp,
//            ),
//            labelSmall = TextStyle(
//                fontFamily = poppinsSemiBold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 10.sp,
//            )
//        )
//
//        return typo
//    }
//}
//
//val LocalTypography = staticCompositionLocalOf<Typography> {
//    error("No Typography provided")
//}