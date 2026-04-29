package com.yumaoem.feature_home.presentation.payments.plan_selection.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R

import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.color.white
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.domain.model.payments.UiPlan
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.components.circularGlow.clickable


@Composable
fun PaymentPlanCard(
    plan: UiPlan,
    isSelectablePlan:Boolean = true,
    onClick: () -> Unit
) {
    val backgroundColor = white
    val borderColor = if (plan.isSelected) LocalColors.current.primary[Colors.TYPE_500.ordinal] else Color.Transparent

    Card(
        modifier = Modifier
            .width(180.dp)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(LocalDimensions.current.dimen20dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.title,
                    style = LocalTypography.current.bodySemiBold
                )
                if (isSelectablePlan){
                    AnimatedContent(
                        targetState = plan.isSelected,
                        transitionSpec = {
                            scaleIn(initialScale = 0.6f) + fadeIn() togetherWith
                                    scaleOut(targetScale = 1f) + fadeOut()
                        },
                        label = "planSelectionAnimation"
                    ) { selected ->
                        Image(
                            modifier = Modifier.size(22.dp),
                            painter = painterResource(
                                if (selected)
                                    R.drawable.ic_selected_plan
                                else
                                    R.drawable.ic_unselected_plan
                            ),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                thickness = .5.dp,
                color = LocalColors.current.neutral[Colors.TYPE_300.ordinal]
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row {
                Image(
                    painter = painterResource(R.drawable.badge_critical_battery),
                    contentDescription = ""

                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = plan.range,
                    style = LocalTypography.current.smallBodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Image(
                    painter = painterResource(R.drawable.calendar_today),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = plan.validity,
                    style = LocalTypography.current.smallBodyMedium
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(
                thickness = .5.dp,
                color = LocalColors.current.neutral[Colors.TYPE_300.ordinal]
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = plan.totalAmount,
                style = LocalTypography.current.bodySemiBold
            )
        }
    }
}


@Preview
@Composable
fun PaymentPlanCardPreview() {
    YumaAppTheme {
        PaymentPlanCard(
            plan = UiPlan(
                id = "323",
                title = "7 Days",
                range = "1000",
                validity = "23",
                totalAmount = "1000",
                amountWithoutTax = "1000",
                taxAmount = "1000",
                isSelected = false
            ),
            onClick = {}
        )
    }
}