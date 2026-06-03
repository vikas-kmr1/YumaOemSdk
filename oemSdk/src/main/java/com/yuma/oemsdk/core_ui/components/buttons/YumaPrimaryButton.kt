package com.yumaoem.core_ui.components.buttons
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.components.Yuma_elevated_card.YumaElevatedCard
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.typography.LocalTypography

@Composable
fun YumaPrimaryButton(
    enabled: Boolean = true,
    buttonText:String,
    buttonTextStyle: TextStyle = LocalTypography.current.bodyLargeSemiBold,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isTrailingIconVisible: Boolean = false,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabledButtonShadowColor: Color = YumaAppTheme.colors.neutral[Colors.TYPE_700.ordinal],
    disabledButtonShadowColor: Color = YumaAppTheme.colors.neutral[Colors.TYPE_400.ordinal],
    enabledButtonContainerColor: Color = YumaAppTheme.colors.neutral[Colors.TYPE_900.ordinal],
    disabledButtonContainerColor: Color = YumaAppTheme.colors.neutral[Colors.TYPE_400.ordinal],
    onClick: () ->Unit,
){
    val shadowColor =
        if (enabled) enabledButtonShadowColor
        else disabledButtonShadowColor

    val containerColor =
        if (enabled)  enabledButtonContainerColor
        else disabledButtonContainerColor

    val buttonTextColor = Color.White

    YumaButtonInternal(
        modifier =  modifier,
        containerColor = containerColor,
        shadowColor = shadowColor,
        isLoading = isLoading,
        onClick = onClick,
        enabled =  enabled,
        buttonTextColor = buttonTextColor,
        buttonText = buttonText,
        buttonTextStyle = buttonTextStyle,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isTrailingIconVisible = isTrailingIconVisible,
        paddingValues = PaddingValues(bottom = 4.dp,top = 0.dp,start = 0.dp,end = 0.dp)
    )
}

@Composable
internal fun YumaButtonInternal(
    modifier: Modifier,
    containerColor: Color,
    shadowColor: Color,
    isLoading: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    buttonTextColor: Color,
    buttonText: String,
    buttonTextStyle: TextStyle = LocalTypography.current.bodyLargeSemiBold,
    paddingValues: PaddingValues,
    leadingIcon: @Composable() (() -> Unit)?,
    trailingIcon: @Composable() (() -> Unit)?,
    isTrailingIconVisible: Boolean
) {
    Box(modifier) {
        YumaElevatedCard(
            shadowColor = containerColor,
            containerColor = shadowColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(YumaAppTheme.dimensions.dimen48dp),
            paddingValues = paddingValues
        ) {
            Button(
                onClick = {
                    if (isLoading.not()) {
                        onClick()
                    }
                },
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    disabledContainerColor = containerColor
                ),
                shape = YumaAppTheme.shapes.buttonShape,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
            ) {
                if (isLoading.not()) {
                    leadingIcon?.invoke()
                    Spacer(modifier= Modifier.width(12.dp))
                    Text(
                        color = buttonTextColor,
                        text = buttonText,
                        style = buttonTextStyle,
                    )
                    if(isTrailingIconVisible) {
                        trailingIcon?.invoke()
                    }
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.then(Modifier.size(32.dp)),
                        color = Color.White
                    )
                }
            }
        }
    }
}