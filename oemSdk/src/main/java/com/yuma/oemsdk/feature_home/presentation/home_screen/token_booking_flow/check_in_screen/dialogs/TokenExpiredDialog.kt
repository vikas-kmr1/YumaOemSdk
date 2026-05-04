package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TokenExpiredModalBottomSheet(
    showSheet: Boolean = true,
    onDismissRequest: () -> Unit,
    onBookingExpiryTryAgainClicked: () -> Unit
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
            TokenExpiredDialogContent(onBookingExpiryTryAgainClicked = onBookingExpiryTryAgainClicked)
        }
    }
}


@Composable
fun TokenExpiredDialog(
    onDismissRequest: () -> Unit,
    onBookingExpiryTryAgainClicked: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties()
    ) {
        Card(
            modifier = Modifier
                .clip(LocalAppShapes.current.dialogShape)
                .background(Color.White)
                .padding(
                    horizontal = LocalDimensions.current.dimen20dp,
                    vertical = LocalDimensions.current.dimen20dp
                ),
        ) {
            TokenExpiredDialogContent(onBookingExpiryTryAgainClicked = onBookingExpiryTryAgainClicked)
        }
    }
}


@Composable
private fun TokenExpiredDialogContent(
    modifier: Modifier = Modifier
        .padding(top = 20.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
    onBookingExpiryTryAgainClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen24dp))
        Image(
            painter = painterResource(R.drawable.ic_red_exclamation_circular),
            contentDescription = null,
            modifier = Modifier.size(LocalDimensions.current.dimen40dp)
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen16dp))
        Text(
            text = "00:00",
            style = LocalTypography.current.heading2SemiBold,
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen8dp))
        Text(
            text = stringResource(R.string.your_booking_has_expired),
            style = LocalTypography.current.heading3SemiBold,
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(36.dp))
        YumaPrimaryButton (
            buttonText = stringResource(R.string.try_again),
            onClick = onBookingExpiryTryAgainClicked,
            trailingIcon = {
                TrailingIcon(
                    trailingIcon = painterResource( R.drawable.ic_retry_white)
                ) {}
            }
        )
    }
}