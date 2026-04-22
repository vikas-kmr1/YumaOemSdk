package com.yuma.oemsdk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.yuma.oemsdk.SdkServiceLocator
import com.yuma.oemsdk.viewmodel.SdkMapViewModel

@Composable
internal fun SdkMapScreen(
    onNavigateToPayments: () -> Unit
) {
    val factory = SdkMapViewModel.Factory(
        prefManager = SdkServiceLocator.prefManager,
        remoteDataSource = SdkServiceLocator.remoteDataSource,
        locationManager = SdkServiceLocator.locationManager
    )
    val viewModel: SdkMapViewModel = viewModel(factory = factory)
    val state by viewModel.mapState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStations()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val initialLocation = SdkServiceLocator.locationManager.currentLocation.collectAsState().value
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(initialLocation?.latitude ?: 12.9716, initialLocation?.longitude ?: 77.5946),
                12f
            )
        }


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = initialLocation != null),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
        ) {
            state.stations.forEach { station ->
                Marker(
                    state = MarkerState(position = LatLng(station.latitude, station.longitude)),
                    title = station.name,
                    snippet = "Available: ${station.availableTokens ?: 0}",
                    onClick = {
                        viewModel.onStationSelected(station)
                        false
                    }
                )
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF00C853))
        }

        // Selected Station Peek / Carousel Placeholder
        state.selectedStation?.let { station ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = station.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Status: ${station.operationStatus ?: "Unknown"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.bookToken(station) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) {
                        Text("Book Battery")
                    }
                }
            }
        }
    }
}
