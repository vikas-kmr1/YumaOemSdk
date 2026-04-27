package com.yumaoem.feature_home.presentation.profile_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.yumaoem.core_ui.components.buttons.YumaSecondaryButton
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LogoutConfirmationBottomSheet(
    onDismissRequest: () -> Unit,
    onLogoutClicked: () -> Unit,
) {
    ModalBottomSheet(
        containerColor = Color.White,
        dragHandle = null,
        onDismissRequest = { onDismissRequest() },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { true }
        ),

        ) {
        LogoutBottomSheetContent(
            onDismissRequest = onDismissRequest,
            onLogoutClicked = onLogoutClicked
        )
    }
}


@Composable
fun LogoutConfirmDialog(
    onDismissRequest: () -> Unit,
    onLogoutClicked: () -> Unit
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
        }
    }
}

@Composable
fun LogoutBottomSheetContent(
    onDismissRequest: () -> Unit,
    onLogoutClicked: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen40dp))

        Text(
            text = stringResource(R.string.want_to_logout),
            style = LocalTypography.current.bodyLargeSemiBold,
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen30dp))
        YumaPrimaryButton(
            buttonText = stringResource(R.string.log_out),
            trailingIcon = {
                TrailingIcon {}
            },
            onClick = onLogoutClicked
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen8dp))
        YumaSecondaryButton(
            buttonText = stringResource(R.string.no),
            onClick = onDismissRequest,
            buttonTextColor = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen40dp))
    }
}

@Composable
fun TrailingIcon(
    trailingIcon: Painter = painterResource(R.drawable.ic_logout_white),
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