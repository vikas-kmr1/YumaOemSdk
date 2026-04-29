package com.yumaoem.core_ui.utils.snackbar.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R

import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography


@Composable
fun ErrorSnackBar(
    data: SnackbarData,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LocalColors.current.red[Colors.TYPE_500.ordinal],
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box{
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp,end = 16.dp,top = 40.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.error_icon),
                        contentDescription = "error icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = data.visuals.message,
                        color = Color.White,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = LocalTypography.current.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Start
                    )
                }
                IconButton(
                    onClick = { data.performAction() },
                    modifier = Modifier.size(24.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_cross_white),
                        contentDescription = "close button"
                    )
                }
            }
        }
    }
}
