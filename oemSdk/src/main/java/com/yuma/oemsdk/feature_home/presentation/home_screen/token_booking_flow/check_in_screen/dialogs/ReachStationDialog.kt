package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
fun ReachStationModalBottomSheet(
    showSheet: Boolean = true,
    title:String,
    buttonText:String,
    onDismissRequest: () -> Unit,
    onOkClicked: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            containerColor = Color.White,
            dragHandle = null,
            onDismissRequest = { onDismissRequest() },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { false }
            ),) {
            ReachStationBottomSheetContent(
                title = title,
                buttonText =  buttonText,
                onOkClicked = onOkClicked
            )
        }
    }
}


@Composable
fun ReachStationWarningDialog(
    title:String,
    buttonText:String,
    onDismissRequest: () -> Unit,
    onOkClicked: () -> Unit
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
                    vertical = LocalDimensions.current.dimen20dp
                ),
        ) {
            ReachStationBottomSheetContent(
               title = title,
               buttonText = buttonText,
               onOkClicked =  onOkClicked,
            )
        }
    }
}


@Composable
private fun ReachStationBottomSheetContent(
    title: String,
    buttonText: String,
    modifier: Modifier = Modifier
        .padding(top = 20.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
    onOkClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google_location_pin),
            contentDescription = null,
            modifier = Modifier.size(LocalDimensions.current.dimen48dp)
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen16dp))
        Text(
            text = title,
            style = LocalTypography.current.bodyLargeSemiBold,
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen30dp))
        YumaPrimaryButton(
            buttonText = buttonText,
            onClick = onOkClicked,
            trailingIcon = {
                if (buttonText == stringResource(R.string.try_again)) {
                    TrailingIcon(
                        trailingIcon = painterResource(R.drawable.ic_retry),
                        onClickTrailingIcon = onOkClicked
                    )
                }
            }
        )
    }
}

@Composable
fun TrailingIcon(
    trailingIcon: Painter = painterResource(R.drawable.ic_cross_red_bg),
    onClickTrailingIcon: () -> Unit
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