package com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi



@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ScanAnotherMachineModalBottomSheet(
    showSheet: Boolean = true,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    if (!showSheet) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        BackHandler(enabled = true) {}

        ErrorBottomSheetContent(
            title = "Scan another machine",
            description = "This machine cannot dispense\na battery right now.",
            ctaText = "Retry",
            onRetry = {
                onRetry()
                onDismiss()
            }
        )
    }
}
