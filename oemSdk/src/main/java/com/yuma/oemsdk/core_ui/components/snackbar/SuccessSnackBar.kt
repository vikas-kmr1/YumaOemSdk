package com.yumaoem.core_ui.components.snackbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.typography.LocalTypography

@Composable
fun SuccessSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        shape = RoundedCornerShape(12.dp),
        containerColor = Color(0xFF49A15F),
        contentColor = Color.White,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = data.visuals.message,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = LocalTypography.current.smallBodyMedium.copy(color = Color.White)
        )
    }
}
