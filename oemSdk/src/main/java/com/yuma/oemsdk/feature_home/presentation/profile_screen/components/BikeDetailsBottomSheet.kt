package com.yumaoem.feature_home.presentation.profile_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuma.oemsdk.R
import com.yumaoem.core.utils.app_utils.getAppVersion
import com.yumaoem.core_ui.components.toolbar_buttons.ToolbarCloseButton
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.color.white
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeDetailsBottomSheet(
    bikeProvider: String,
    bikeNumber: String,
    qrNumber: String,
    batteryIds: List<BatteryDetails>,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val appVersion = remember { getAppVersion() }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = {},
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BikeDetailsHeader(onDismissRequest)

            Spacer(modifier = Modifier.height(16.dp))

            BikeInfoCard(
                bikeProvider = bikeProvider,
                bikeNumber = bikeNumber,
                qrNumber = qrNumber
            )

            Spacer(modifier = Modifier.height(24.dp))

            BatteryListCard(batteryIds)

            Text(
                text = "Version $appVersion",
                style = LocalTypography.current.smallBody.copy(
                    fontSize = 12.sp,
                    color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
                ),
                modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
            )
        }
    }
}

@Composable
private fun BikeDetailsHeader(
    onDismissRequest: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Bike Details",
            style = LocalTypography.current.bodyLargeSemiBold
        )
        ToolbarCloseButton(onClick = onDismissRequest)
    }
}

@Composable
private fun BikeInfoCard(
    bikeProvider: String,
    bikeNumber: String,
    qrNumber: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LocalColors.current.primary[Colors.TYPE_300.ordinal])
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KeyValueRow(label = "Bike Provider:", value = bikeProvider)
            Spacer(modifier = Modifier.height(4.dp))
            KeyValueRow(label = "Bike Number:", value = bikeNumber)

            Spacer(modifier = Modifier.height(16.dp))

            TokenQRCodeView(
                qrCodeValue = qrNumber,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "QR Number : $qrNumber",
                style = LocalTypography.current.smallBodySemiBold,
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        }
    }
}

@Composable
private fun KeyValueRow(
    label: String,
    value: String
) {
    Row {
        Text(
            text = "$label ",
            style = LocalTypography.current.smallBodySemiBold,
            color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
        )
        Text(
            text = value,
            style = LocalTypography.current.smallBodySemiBold,
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
        )
    }
}
@Composable
private fun BatteryListCard(
    batteryIds: List<BatteryDetails>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LocalColors.current.primary[Colors.TYPE_300.ordinal])
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Batteries in bike (${batteryIds.size})",
                style = LocalTypography.current.bodySemiBold,
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )

            Spacer(modifier = Modifier.height(8.dp))

            batteryIds.forEach { battery ->
                BatteryItem(battery)
            }
        }
    }
}

@Composable
private fun BatteryItem(
    battery: BatteryDetails
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_battery_charge),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Battery ID: ",
                style = LocalTypography.current.smallBodyMedium,
                color = LocalColors.current.neutral[Colors.TYPE_600.ordinal]
            )
            Text(
                text = battery.qrCode,
                fontWeight = FontWeight.Bold,
                style = LocalTypography.current.smallBodyMedium,
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        }
    }
}


private val previewBatteries = listOf(
    BatteryDetails(qrCode = "BAT-001",     itemGroupId = 3,
        soc = 3.0,
        soh = 34.3,
        soe = 23.5),
    BatteryDetails(
        qrCode = "BAT-002",
        itemGroupId = 3,
        soc = 3.0,
        soh = 34.3,
        soe = 23.5
    )
)

@Preview()
@Composable
fun BikeDetailsBottomSheetPreview() {
    YumaAppTheme {
        BikeDetailsBottomSheet(
            bikeProvider = "Yulu",
            bikeNumber = "YULU-456",
            qrNumber = "QR-123456",
            batteryIds = previewBatteries,
            onDismissRequest = {},
        )
    }
}

