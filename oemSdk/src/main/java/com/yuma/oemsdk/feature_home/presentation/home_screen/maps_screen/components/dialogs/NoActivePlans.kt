package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.dialogs

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.components.error_bottom_sheet.YumaErrorBottomSheetContent


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NoActivePlansModalBottomSheet(
    showSheet: Boolean = true,
    onDismissRequest: () -> Unit,
    onPurchasePlanClicked: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            containerColor = Color.White,
            dragHandle = null,
            onDismissRequest = { onDismissRequest() },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { false }
            ),
            ) {
            YumaErrorBottomSheetContent(
                imageRes = painterResource(R.drawable.ic_exclamation_circle_red),
                title = "No Active Plan",
                subtitle = "Purchase a plan to swap batteries",
                onButtonClick = onPurchasePlanClicked,
                buttonText = "Purchase Plan"
            )
        }
    }
}
