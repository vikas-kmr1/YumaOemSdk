package com.yumaoem.core_ui.components.otp_view

/**
 * created by Maroof Ansari
 *
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography

@Composable
fun YumaOtpView(
    smsCodeLength: Int,
    showError:Boolean = false,
    modifier: Modifier = Modifier,
    errorMessage: String = "Error Message to be replaced later",
    errorColor: Color = Color.Red,
    focusedBorderColor: Color = LocalColors.current.neutral[Colors.TYPE_700.ordinal],
    unfocusedBorderColor: Color = LocalColors.current.neutral[Colors.TYPE_700.ordinal],
    textStyle:TextStyle = LocalTypography.current.bodyLargeMedium,
    smsFulled: (String) -> Unit,
    onNumberDeleted: () -> Unit,
) {
    val focusRequesters: List<FocusRequester> = remember {
        (0 until smsCodeLength).map { FocusRequester() }
    }
    val enteredNumbers = remember {
        mutableStateListOf(
            *((0 until smsCodeLength).map { "" }.toTypedArray())
        )
    }
    val textColor: Color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
    Column {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            for (index in 0 until smsCodeLength) {
                OutlinedTextField(
                    modifier = Modifier
                        .width(56.dp)
                        .height(56.dp)
                        .focusRequester(focusRequesters[index])
                        .focusOrder(focusRequester = focusRequesters[index])
                        .onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown &&
                                event.key == Key.Backspace &&
                                enteredNumbers[index].isEmpty()
                            ) {
                                // jump left and clear the previous cell
                                focusRequesters.getOrNull(index - 1)?.requestFocus()
                                enteredNumbers.getOrNull(index - 1)?.let {
                                    enteredNumbers[index - 1] = ""
                                }
                                true      // consume ⌫
                            } else false
                        },
                    textStyle = textStyle,
                    singleLine = true,
                    value = enteredNumbers.getOrNull(index)?.trim() ?: "",
                    maxLines = 1,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        disabledTextColor = textColor,

                        // Cursor colors
                        cursorColor = if (showError) errorColor else textColor,
                        errorCursorColor = errorColor,

                        // Indicator (border) colors
                        focusedIndicatorColor = if (showError) errorColor else focusedBorderColor,
                        unfocusedIndicatorColor = if (showError) errorColor else unfocusedBorderColor,
                        disabledIndicatorColor = unfocusedBorderColor.copy(alpha = 0.38f),
                        errorIndicatorColor = errorColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    shape = YumaAppTheme.shapes.textFieldShape,
                    onValueChange = { new: String ->
                        when {
                            isNumeric(new) -> {
                                when {
                                    // Insert a digit
                                    new.length == 1 -> {
                                        enteredNumbers[index] = new
                                        focusRequesters.getOrNull(index + 1)?.requestFocus()
                                    }
                                    // Delete the digit that *was* inside this box
                                    new.isEmpty() && enteredNumbers[index].isNotEmpty() -> {
                                        enteredNumbers[index] = ""
                                        onNumberDeleted()
                                    }
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                    ),
                )
                val fulled = enteredNumbers.joinToString(separator = "")
                if (fulled.length == smsCodeLength) {
                    smsFulled.invoke(fulled)
                }
                if (index!=smsCodeLength-1){
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
        if (showError) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = errorMessage,
                    color = errorColor
                )
            }
        }
    }

}

fun isNumeric(toCheck: String): Boolean {
    return toCheck.all { char -> char.isDigit() }
}