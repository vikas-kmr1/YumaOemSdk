package com.yumaoem.feature_home.presentation.diy_flow.dialogs


import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.yuma.oemsdk.core_ui.utils.ImageConstants
import com.yumaoem.core_ui.theme.YumaAppTheme


@Composable
fun SwapInfoDialog(
    title: String,
    onAction: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        content = {
            AppDialog(
                titleText = title,
                dialogHeight = 438.dp,
                dialogWidth = 320.dp,
                spacerHeight = 25.dp,
                verticalPadding = 28.dp,
                imageComposable = {
                    SwapInfoImageComposable()
                },
                description = listOf(
                    "1. Scan another machine",
                    "2. Insert 2nd Battery"
                ),
                buttonText = "Scan QR",
                onButtonClick = {
                    onAction()
                }
            )
        }
    )
}

@Composable
fun SwapInfoImageComposable(){
    AsyncImage(
        model = ImageConstants.YCU_SCAN_URL,
        contentDescription = "ycu Scan Image",
        modifier = Modifier
            .size(162.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Preview
@Composable
fun SwapInfoDialogPreview() {
    YumaAppTheme {
        SwapInfoDialog(
            title = "Battery 2",
            onAction = {

            }
        )
    }
}