package com.yumaoem.feature_home.presentation.payments.payment_details

import androidx.activity.compose.BackHandler
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
fun CouldNotFetchPaymentStatus(
    showSheet: Boolean = true,
    onBackClick: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            containerColor = Color.White,
            dragHandle = null,
            onDismissRequest = {  },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { false }
            ),
        ) {
            BackHandler(enabled = true) {}
            YumaErrorBottomSheetContent(
                imageRes = painterResource(R.drawable.ic_exclamation_circle_red),
                title = "Could not fetch payment status",
                subtitle = "please go back to refresh",
                onButtonClick = onBackClick,
                buttonText = "Go back"
            )
        }
    }
}
