package com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SomethingWentWrongModalBottomSheet(
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
        dragHandle = null,
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        BackHandler(enabled = true) {}

        ErrorBottomSheetContent(
            title = "Something went wrong",
            description = "Please scan the YCU again",
            onRetry = {
                onRetry()
                onDismiss()
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BluetoothConnectionFailedModalBottomSheet(
    showSheet: Boolean = true,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.Hidden }
            )

        ) {
            BackHandler(enabled = true) {}
            ErrorBottomSheetContent(
                title = "Bluetooth connection failed",
                description = "Please scan the YCU again",
                onRetry = {
                    onRetry()
                    onDismiss()
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncorrectModalBottomSheet(
    showSheet: Boolean = true,
    onRetry: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = {},
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.Hidden }
            )
        ) {
            ErrorBottomSheetContent(
                title = "Incorrect QR",
                description = "Please scan the QR code on the YCU",
                onRetry = {
                    onRetry()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanBatteryTryAgainModalBottomSheet(
    showSheet: Boolean = true,
    onRetry: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = {},
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.Hidden }
            )
        ) {
            ErrorBottomSheetContent(
                title = "Try Again",
                description = "Please scan the battery again",
                onRetry = {
                    onRetry()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DBInsertFailedModalBottomSheet(
    showSheet: Boolean = true,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    if(showSheet){
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            ErrorBottomSheetContent(
                title = "Please insert battery",
                description = "Insert your discharged battery and complete swap",
                onRetry = {
                    onRetry()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DBInsertFailedCustomerSupportModalBottomSheet(
    showSheet: Boolean = true,
    onCustomerSupportClicked: () -> Unit,
    onScanMachine: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Color.Black.copy(alpha = 0.4f)
                            )
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_cross_white),
                            contentDescription = "Close",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            onDismissRequest = {
                onDismiss()
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            containerColor = Color.Transparent,
        ) {
            Column(modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
            ) {
                SecondaryErrorBottomSheetContent(
                    title = "Something went wrong",
                    onScanMachine = {
                        onDismiss()
                        onScanMachine()
                    },
                    onCustomerSupportClicked = {
                        onCustomerSupportClicked()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CBOpenFailedModalBottomSheet(
    showSheet: Boolean = true,
    onCustomerSupportClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    if(showSheet){
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            ContactYumaSupportBottomSheetContent(
                title = "Contact Yuma support",
                description = "Call us to complete your swap",
                onCustomerSupportClicked = {
                    onCustomerSupportClicked()
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapStatusNotCompletedModalBottomSheet(
    showSheet: Boolean = true,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
){
    if (showSheet) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            ErrorBottomSheetContent(
                title = "Swap in progress",
                description = "Please remove charged battery and complete swap",
                onRetry = {
                    onRetry()
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteSwapInProgressModalBottomSheet(
    showSheet: Boolean = true,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
){
    if (showSheet) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            ErrorBottomSheetContent(
                title = "Swap in progress",
                description = "Please wait for your swap to complete",
                onRetry = {
                    onRetry()
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DBDoNotInsertBatteryModalSheet(
    showSheet: Boolean = true,
    onCustomerSupportClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    if(showSheet){
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            ContactYumaSupportBottomSheetContent(
                title = "Do NOT insert battery",
                description = "Close the door without battery",
                onCustomerSupportClicked = {
                    onCustomerSupportClicked()
                }
            )
        }
    }
}

