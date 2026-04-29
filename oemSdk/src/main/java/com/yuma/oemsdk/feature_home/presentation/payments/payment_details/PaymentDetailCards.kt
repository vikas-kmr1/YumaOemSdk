package com.yumaoem.feature_home.presentation.payments.payment_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuma.oemsdk.R

import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.color.white
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.components.circularGlow.clickable


@Composable
fun PlanCard(
    swaps: String,
    days: String,
    price: String,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = swaps,
                    style = LocalTypography.current.smallBodyMedium
                )
                Spacer(modifier = Modifier.width(12.dp))

                VerticalDivider(
                    thickness = 1.dp,
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                    modifier = Modifier.height(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = days,
                    style = LocalTypography.current.smallBodyMedium
                )

            }

            Text(
                text = price,
                style = LocalTypography.current.bodyLargeSemiBold
            )
        }
    }
}

@Composable
fun PaymentMethodItem(
    onClick: () -> Unit
) {
    val backgroundColor = white
    val borderColor = LocalColors.current.primary[Colors.TYPE_500.ordinal]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Online",
                    style = LocalTypography.current.smallBodySemiBold
                        .copy()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "UPI, Card, Netbanking",
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                        fontSize = 10.sp
                    )
                )
            }
            Image(
                painter = painterResource(R.drawable.ic_selected_plan),
                contentDescription = "",
                modifier = Modifier

            )
        }
    }
}

@Composable
fun BillDetailsCard(
    planAmount: String,
    gstAndTax: String,
    totalAmount: String
) {
    val backgroundColor = white
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 20.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Plan Amount",
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                    )
                )
                Text(
                    text = planAmount,
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "GST & Taxes",
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                    )
                )
                Text(
                    text = gstAndTax,
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = LocalDimensions.current.dimenHalfDp,
                color = Color(0xFFD9D9D9)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total",
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                    )
                )
                Text(
                    text = totalAmount,
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                    )
                )
            }
        }
    }
}


@Preview()
@Composable
fun PlanCardPreview() {
    YumaAppTheme {
        PlanCard(
            swaps = "10 Swaps",
            days = "30 Days",
            price = "499"
        )
    }
}

@Preview()
@Composable
fun PaymentMethodItemPreview() {
    YumaAppTheme {
        PaymentMethodItem(
            onClick = {}
        )
    }
}

@Preview()
@Composable
fun BillDetailsCardPreview() {
    YumaAppTheme {
        BillDetailsCard(
            planAmount = "₹499",
            gstAndTax = "₹49",
            totalAmount = "₹548"
        )
    }
}
