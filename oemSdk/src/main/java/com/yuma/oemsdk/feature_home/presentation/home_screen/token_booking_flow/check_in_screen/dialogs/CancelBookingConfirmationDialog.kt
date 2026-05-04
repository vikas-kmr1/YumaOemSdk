package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yuma.oemsdk.R

import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.components.buttons.YumaSecondaryButton

import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.profile_screen.components.TrailingIcon


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CancelBookingConfirmationModalBottomSheet(
    showSheet: Boolean = true,
    onDismissRequest: () -> Unit,
    onCancelBookingConfirmed: () -> Unit
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
            BackHandler(enabled = true) {}
            CancelBookingConfirmationContent(
                onDismissRequest = onDismissRequest,
                onCancelBookingConfirmed = onCancelBookingConfirmed
            )
        }
    }
}


@Composable
fun CancelBookingConfirmationDialog(
    onDismissRequest: () -> Unit,
    onCancelBookingConfirmed: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties()
    ) {
        Card(
            modifier = Modifier
                .clip(LocalAppShapes.current.dialogShape)
                .background(Color.White)
                .padding(
                    horizontal = LocalDimensions.current.dimen20dp,
                    vertical = LocalDimensions.current.dimen12dp
                ),
        ) {
            CancelBookingConfirmationContent(onDismissRequest =onDismissRequest, onCancelBookingConfirmed = onCancelBookingConfirmed)
        }
    }
}


@Composable
private fun CancelBookingConfirmationContent(
    modifier: Modifier  = Modifier
        .padding(top = 20.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
    onDismissRequest: () -> Unit,
    onCancelBookingConfirmed: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
        Text(
            text = "Are you sure you want to\n cancel your token?",
            style = LocalTypography.current.bodyLargeSemiBold,
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen30dp))
        YumaPrimaryButton(
            buttonText = "Yes, Cancel",
            enabledButtonContainerColor = LocalColors.current.red[Colors.TYPE_500.ordinal],
            enabledButtonShadowColor = LocalColors.current.red[Colors.TYPE_600.ordinal],
            trailingIcon = {
                TrailingIcon(
                    trailingIcon = painterResource(R.drawable.ic__red_cross_filled_white_bg),
                    onClickTrailingIcon = onCancelBookingConfirmed
                )
            },
            onClick = onCancelBookingConfirmed
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
        YumaSecondaryButton(
            buttonText = stringResource(R.string.no),
            onClick = onDismissRequest,
            buttonTextColor = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
        )
    }
}