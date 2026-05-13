package com.yuma.oemsdk.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.ErrorBottomSheetContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun WrongBatteryBottomSheet(
    showSheet: Boolean = true,
    onDismissRequest: () -> Unit,
    onTryAgainClicked: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            containerColor = Color.White,
            dragHandle = null,
            onDismissRequest = { onDismissRequest() },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.Hidden }
            ),
        ) {
            BackHandler {
                onTryAgainClicked()
                onDismissRequest()
            }
            ErrorBottomSheetContent(
                title = "Wrong Battery",
                description = "Scan the battery given in the previous swap.",
                onRetry = {
                    onTryAgainClicked()
                    onDismissRequest()
                }
            )
        }
    }
}
