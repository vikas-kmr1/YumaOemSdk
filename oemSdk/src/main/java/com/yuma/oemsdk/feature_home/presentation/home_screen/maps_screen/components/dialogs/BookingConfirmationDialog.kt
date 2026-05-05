package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.components.buttons.YumaSecondaryButton
import com.yumaoem.core_ui.components.toolbar_buttons.ToolbarCloseButton
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography



@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BookingConfirmationModalBottomSheet(
    title: String,
    showSheet: Boolean = true,
    isBookingInProgress: Boolean = false,
    onDismissRequest: () -> Unit,
    onBookNowClicked: () -> Unit,
) {
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                if (!isBookingInProgress) {
                    onDismissRequest()
                }
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { it != SheetValue.Hidden }
            ),

            ) {
            BackHandler(enabled = true) {}
            BookingConfirmationContent(
                isBookingInProgress = isBookingInProgress,
                onDismissRequest = onDismissRequest,
                title = title,
                onBookNowClicked = onBookNowClicked
            )
        }
    }
}

@Composable
fun BookingConfirmationDialog(
    title: String,
    isBookingInProgress: Boolean = false,
    onDismissRequest: () -> Unit,
    onBookNowClicked: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            if (!isBookingInProgress) {
                onDismissRequest()
            }
        },
        properties = DialogProperties(),
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
            BookingConfirmationContent(
                isBookingInProgress = isBookingInProgress,
                onDismissRequest = onDismissRequest,
                title = title,
                modifier = Modifier
                    .fillMaxWidth(),
                onBookNowClicked = onBookNowClicked
            )
        }
    }
}

@Composable
private fun BookingConfirmationContent(
    isBookingInProgress: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    onBookNowClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ToolbarCloseButton {
                if (!isBookingInProgress) {
                    onDismissRequest()
                }
            }
        }
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
        Text(
            text = title,
            style = LocalTypography.current.heading5SemiBold,
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
        YumaPrimaryButton(
            isLoading = isBookingInProgress,
            buttonText = stringResource(R.string.book_now),
            trailingIcon = {
                TrailingIcon {}
            },
            onClick = onBookNowClicked
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
        YumaSecondaryButton(
            enabled = isBookingInProgress.not(),
            buttonText = stringResource(R.string.no),
            buttonTextColor = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
            onClick = onDismissRequest,
        )
    }
}

@Composable
private fun TrailingIcon(
    trailingIcon: Painter = painterResource(R.drawable.ic_arrow_right),
    onClickTrailingIcon: () -> Unit,
) {
    IconButton(
        onClick = { onClickTrailingIcon() }
    ) {
        Image(
            modifier = Modifier
                .size(LocalDimensions.current.dimen20dp),
            painter = trailingIcon,
            contentDescription = null,
            contentScale = ContentScale.Inside,
        )
    }
}