package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.yuma.oemsdk.R
import com.yumaoem.core.utils.noRippleDebounceClickable
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography

@Composable
fun getDirectionsMapView(
    backgroundImage:String = "https://yuma-static.s3.ap-south-1.amazonaws.com/oem/maps_photo+(1).png",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Box(
        modifier = modifier
            .noRippleDebounceClickable {
                onClick()
            }
            .fillMaxWidth()
            .height(LocalDimensions.current.dimen110dp)
            .clip(LocalAppShapes.current.buttonShape)
            .background(Color.White)
    ) {
        AsyncImage(
            model = backgroundImage,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )
        GetDirectionsChipView(
            modifier = Modifier.align(Alignment.Center),
            onClick = onClick
        )
    }
}

@Composable
fun GetDirectionsChipView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .noRippleDebounceClickable { onClick() }
            .clip(LocalAppShapes.current.buttonShape)
            .background(Color.White)
            .padding(vertical = LocalDimensions.current.dimen10dp)
            .padding(horizontal = LocalDimensions.current.dimen20dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Get Directions",
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
            style = LocalTypography.current.bodyLargeSemiBold
        )
        Image(
            painter = painterResource(R.drawable.google_map_pin_logo),
            contentDescription = ""
        )
    }
}