package com.yuma.oemsdk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yuma.oemsdk.SdkServiceLocator
import com.yuma.oemsdk.viewmodel.SdkProfileEvent
import com.yuma.oemsdk.viewmodel.SdkProfileViewModel
import com.yuma.oemsdk.viewmodel.SdkSwapItemUiModel

@Composable
internal fun SdkProfileTabContent(
    prefManager: com.yuma.oemsdk.prefs.SdkPrefManager,
    onLogout: () -> Unit
) {
    val factory = rememberViewModelFactory(prefManager, SdkServiceLocator.remoteDataSource)
    val viewModel: SdkProfileViewModel = viewModel(factory = factory)
    
    val state by viewModel.state.collectAsState()
    val event by viewModel.events.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onTabVisible()
    }

    LaunchedEffect(event) {
        when (event) {
            is SdkProfileEvent.LoggedOut -> {
                viewModel.consumeEvent()
                onLogout()
            }
            is SdkProfileEvent.Error -> {
                // Handle error (e.g. snackbar)
                viewModel.consumeEvent()
            }
            null -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp)
    ) {
        // ── Header ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = state.profileImageUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.fullName.ifEmpty { "User" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.phone,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Swap History ──
        Text(
            text = "Swap History",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.swapHistory.isEmpty() && !state.isLoadingMoreSwaps) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No swap history", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.swapHistory) { day ->
                    Text(
                        text = day.date,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    day.swaps.forEach { swap ->
                        SdkSwapHistoryItem(swap)
                    }
                }
                if (state.isLoadingMoreSwaps) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF00C853))
                        }
                    }
                }
            }
        }

        // ── Logout ──
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        TextButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Log out", color = Color(0xFFD32F2F), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SdkSwapHistoryItem(record: SdkSwapItemUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = record.swapTime,
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
        Text(
            text = record.serviceTime,
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
private fun rememberViewModelFactory(
    prefManager: com.yuma.oemsdk.prefs.SdkPrefManager,
    remoteDataSource: com.yuma.oemsdk.data.network.SdkHomeRemoteDataSource
): androidx.lifecycle.ViewModelProvider.Factory {
    return androidx.compose.runtime.remember {
        SdkProfileViewModel.Factory(prefManager, remoteDataSource)
    }
}
