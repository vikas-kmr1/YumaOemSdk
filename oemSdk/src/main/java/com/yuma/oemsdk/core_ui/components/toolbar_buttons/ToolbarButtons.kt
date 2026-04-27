package com.yumaoem.core_ui.components.toolbar_buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yumaoem.core.utils.noRippleDebounceClickable
import com.yuma.oemsdk.R


@Composable
fun ToolbarBackButton(
    modifier: Modifier = Modifier,
    onClick: ()-> Unit
){
    Box(
        modifier = modifier
            .size(40.dp)
            .noRippleDebounceClickable {
                onClick()
            }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_toolbar_back),
            contentDescription = "close button",
        )
    }
}



@Composable
fun ToolbarCloseButton(
    modifier: Modifier = Modifier,
    onClick: ()-> Unit
){
    Box(
        modifier = modifier
            .size(40.dp)
            .noRippleDebounceClickable {
                onClick()
            }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_btn_close),
            contentDescription = "close button",
        )
    }
}