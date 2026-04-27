package com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.components.text_field.OutlinedTextFieldWithIcon
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GetCallbackContentModalBottomSheet(
    mobileNumber: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    showSheet: Boolean = true,
    onDismiss: () -> Unit,
    onReceiveCallClicked: (String) -> Unit
) {
    val contactNumber = remember {
        mutableStateOf(mobileNumber)
    }
    if (showSheet) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { false }
            ),
        ) {
            GetCallbackContent(
                mobileNumber = contactNumber.value,
                isLoading = isLoading,
                modifier = modifier,
                onReceiveCallClicked = {
                    onReceiveCallClicked(contactNumber.value)
                },
                onInputChange = {
                    contactNumber.value = it
                }
            )
        }
    }
}


@Composable
fun GetCallbackContent(
    mobileNumber: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onReceiveCallClicked: () -> Unit,
    onInputChange: (String) -> Unit = {},
) {
    val isButtonEnabled: State<Boolean> = remember {
        derivedStateOf {
            mobileNumber.length == 10
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                top = LocalDimensions.current.dimen24dp,
                bottom = LocalDimensions.current.dimen30dp,
                start = LocalDimensions.current.dimen20dp,
                end = LocalDimensions.current.dimen20dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.get_a_call_back),
            style = LocalTypography.current.bodyLargeSemiBold.copy(
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen2dp))
        Text(
            text = stringResource(R.string.our_team_will_call_you_now),
            style = LocalTypography.current.bodyMedium.copy(
                color = Color(0xFF717171)
            )
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen28dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = LocalColors.current.primary[Colors.TYPE_300.ordinal],
                    shape = LocalAppShapes.current.textFieldShape
                )
                .padding(
                    horizontal = LocalDimensions.current.dimen24dp,
                    vertical = LocalDimensions.current.dimen16dp
                )
        ) {
            Text(
                text = stringResource(R.string.confirm_your_number),
                style = LocalTypography.current.smallBodyMedium.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                )
            )
            Spacer(modifier = Modifier.height(LocalDimensions.current.dimen8dp))
            OutlinedTextFieldWithIcon(
                leadingIconView = {
                    Text(
                        "+91", style = LocalTypography.current.bodyLargeMedium.copy(
                            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                            textAlign = TextAlign.Left
                        )
                    )
                },
                value = mobileNumber,
                onValueChange = {
                    val text = it.take(10)
                    if (text.all { char -> char.isDigit() }) {
                        onInputChange(text)
                    }
                },
                placeholderText = "",
                errorIcon = painterResource(R.drawable.yuma_logo),
                errorColor = Color.Red,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen24dp))

        YumaPrimaryButton(
            isLoading = isLoading,
            enabled = isButtonEnabled.value,
            buttonText = stringResource(R.string.receive_call),
            buttonTextStyle = LocalTypography.current.bodySemiBold,
            onClick = onReceiveCallClicked
        )
    }
}


@Preview
@Composable
fun GetCallbackContentPreview() {
    YumaAppTheme {
        GetCallbackContent(
            mobileNumber = "+91 9876543210",
            isLoading = false,
            onReceiveCallClicked = {

            }
        )
    }
}