package com.yumaoem.feature_home.presentation.diy_flow.scan_qr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun QRNumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onEnterPressed: () -> Unit,
    keyboardIcon: Painter,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter QR number"
) {
    val containerColor = Color(0x80929392)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 16.sp,
                color = Color.White
            )
        },
        leadingIcon = {
            Icon(
                painter = keyboardIcon,
                contentDescription = "Keyboard",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = containerColor,
            unfocusedBorderColor = containerColor,
            cursorColor = Color.White,
            focusedLeadingIconColor = Color.White,
            unfocusedLeadingIconColor = Color.White.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(30.dp),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = Color.White
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Characters
        ),
        keyboardActions = KeyboardActions(
            onDone = { onEnterPressed() }
        )
    )
}
