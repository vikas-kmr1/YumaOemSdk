package com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R
import com.yumaoem.core.utils.noRippleDebounceClickable

import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton

import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography


@Composable
fun ErrorBottomSheetContent(
    title: String,
    description: String,
    ctaText: String = "Try Again",
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 38.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_exclamation_circle_red),
            contentDescription = "",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyLargeSemiBold.copy(
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            style = LocalTypography.current.bodyMedium.copy(
                color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        YumaPrimaryButton(
            buttonText = ctaText,
            buttonTextStyle = LocalTypography.current.bodySemiBold,
            onClick = onRetry
        )
    }
}

@Composable
fun SecondaryErrorBottomSheetContent(
    title: String,
    ctaText: String = "Scan machine again",
    onScanMachine: () -> Unit,
    onCustomerSupportClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 38.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_exclamation_circle_red),
            contentDescription = "",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyLargeSemiBold.copy(
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        )
        Spacer(modifier = Modifier.height(28.dp))
        YumaPrimaryButton(
            buttonText = ctaText,
            buttonTextStyle = LocalTypography.current.bodySemiBold,
            onClick = onScanMachine
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomerSupportButton(
            onCustomerSupportClicked = onCustomerSupportClicked
        )
    }
}

@Composable
fun ContactYumaSupportBottomSheetContent(
    title: String = "Contact Yuma support",
    description: String = "Call us to complete your swap",
    onCustomerSupportClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 38.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_exclamation_circle_red),
            contentDescription = "",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyLargeSemiBold.copy(
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            style = LocalTypography.current.bodyMedium.copy(
                color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        YumaPrimaryButton(
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_phone_outlined),
                    contentDescription = "phone icon",
                    colorFilter = ColorFilter.tint(Color.White)
                )
            },
            buttonText = "Customer Support",
            buttonTextStyle = LocalTypography.current.bodySemiBold,
            onClick = onCustomerSupportClicked
        )
    }
}

@Composable
fun CustomerSupportButton(
    onCustomerSupportClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .noRippleDebounceClickable(
                onClick = onCustomerSupportClicked
            )
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                shape = RoundedCornerShape(38.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.5.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_phone_outlined),
            contentDescription = "phone icon"
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Customer Support",
            style = LocalTypography.current.bodySemiBold.copy(
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        )
    }
}


/**  Previews **/

@Composable
@Preview
fun SomethingWentWrongPreview() {
    YumaAppTheme {
        Column(
            modifier = Modifier.background(Color.White)
        ) {
            ErrorBottomSheetContent(
                title = "Something went wrong",
                description = "Please scan the YCU again",
                onRetry = {}
            )
        }
    }
}

@Composable
@Preview
fun SecondaryErrorPreview() {
    YumaAppTheme {
        Column(
            modifier = Modifier.background(Color.White)
        ) {
            SecondaryErrorBottomSheetContent(
                title = "Something went wrong",
                onScanMachine = {},
                onCustomerSupportClicked = {}
            )
        }
    }
}

@Composable
@Preview
fun ContactYumaSupportBottomSheetContentPreview() {
    YumaAppTheme {
        Column(
            modifier = Modifier.background(Color.White)
        ) {
            ContactYumaSupportBottomSheetContent(
                onCustomerSupportClicked = {}
            )
        }
    }
}



