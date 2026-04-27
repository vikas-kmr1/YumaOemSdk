package com.yumaoem.core_ui.components.text_field

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.color.color_D3D9E0
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography


@Composable
fun OutlinedTextFieldWithIcon(
    value: String,
    placeholderText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
    placeHolderTextColor: Color = color_D3D9E0,
    haveTrailingIcon: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    focusedBorderColor: Color = LocalColors.current.neutral[Colors.TYPE_700.ordinal],
    unfocusedBorderColor: Color = LocalColors.current.neutral[Colors.TYPE_700.ordinal],
    showError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    errorMessage: String = "Error Message to be replaced later",
    errorIcon: Painter = painterResource(R .drawable.right_icon),
    errorColor: Color = Color.Red,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onFocusChanged: (FocusState) -> Unit = {},
    leadingIconView: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .onFocusChanged { onFocusChanged(it) },
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholderText,
                    color = if (showError) errorColor else placeHolderTextColor,
                    style = LocalTypography.current.bodyLargeMedium.copy(
                        color = textColor,
                        textAlign = TextAlign.Left
                    )
                )
            },
            textStyle = LocalTypography.current.bodyLargeMedium.copy(
              color = textColor,
              textAlign = TextAlign.Left
            ),
            colors = TextFieldDefaults.colors(
                // Text colors
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                disabledTextColor = textColor.copy(alpha = 0.38f),
                errorTextColor = errorColor,

                // Cursor colors
                cursorColor = if (showError) errorColor else textColor,
                errorCursorColor = errorColor,

                // Indicator (border) colors
                focusedIndicatorColor = if (showError) errorColor else focusedBorderColor,
                unfocusedIndicatorColor = if (showError) errorColor else unfocusedBorderColor,
                disabledIndicatorColor = unfocusedBorderColor.copy(alpha = 0.38f),
                errorIndicatorColor = errorColor,

                // Container (background) colors - SET TO TRANSPARENT
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,

                // Placeholder
                focusedPlaceholderColor = if (showError) errorColor else placeHolderTextColor,
                unfocusedPlaceholderColor = if (showError) errorColor else placeHolderTextColor,
                disabledPlaceholderColor = placeHolderTextColor.copy(alpha = 0.38f),
                errorPlaceholderColor = errorColor,

                // Label colors
                focusedLabelColor = if (showError) errorColor else focusedBorderColor,
                unfocusedLabelColor = if (showError) errorColor else unfocusedBorderColor,
                disabledLabelColor = unfocusedBorderColor.copy(alpha = 0.38f),
                errorLabelColor = errorColor,

                // Trailing icon colors
                focusedTrailingIconColor = if (showError) errorColor else textColor,
                unfocusedTrailingIconColor = if (showError) errorColor else textColor,
                errorTrailingIconColor = errorColor,
                disabledTrailingIconColor = textColor.copy(alpha = 0.38f),
            ),
            enabled = enabled,
            readOnly = readOnly,
            singleLine = true,
            leadingIcon = leadingIconView,
            trailingIcon = trailingIcon ?: if (haveTrailingIcon) {
                { TrailingIcon { onValueChange("") } }
            } else null,
            isError = showError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            shape = LocalAppShapes.current.textFieldShape
        )

        if (showError) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Image(
//                    modifier = Modifier.size(16.dp),
//                    painter = errorIcon,
//                    contentDescription = null,
//                    contentScale = ContentScale.Inside
//                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = errorMessage,
                    color = errorColor
                )
            }
        }
    }
}

@Composable
fun TrailingIcon(
    modifier: Modifier = Modifier,
    trailingIcon: Painter = painterResource(R.drawable.ic_arrow_right),
    onClickTrailingIcon: () -> Unit
) {
    IconButton(onClick = { onClickTrailingIcon() }) {
        Image(
            modifier = Modifier
                .size(20.dp),
            painter = trailingIcon,
            contentDescription = null,
            contentScale = ContentScale.Inside
        )
    }
}
