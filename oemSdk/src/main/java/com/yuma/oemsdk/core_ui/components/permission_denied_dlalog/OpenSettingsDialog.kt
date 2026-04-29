package com.yumaoem.core_ui.components.permission_denied_dlalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography

@Composable
fun OpenSettingsDialog(
    title:String = "",
    onDismissRequest: () -> Unit,
    onOpenSettingsClicked: () -> Unit
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(LocalDimensions.current.dimen12dp))
                Text(
                    text = stringResource(R.string.permissions_denied),
                    style = LocalTypography.current.heading5SemiBold,
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.dimen12dp))
                Text(
                    text = title,
                    style = LocalTypography.current.bodyMedium,
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
                YumaPrimaryButton(
                    buttonText = stringResource(R.string.open_settings),
                    onClick = onOpenSettingsClicked
                )
            }
        }
    }
}