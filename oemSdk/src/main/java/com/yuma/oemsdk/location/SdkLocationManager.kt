package com.yuma.oemsdk.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "SdkLocationManager"

/**
 * SDK-internal location manager. Wraps FusedLocationProviderClient and
 * exposes the current location as a [StateFlow].
 *
 * Does not require Koin or any DI framework — constructed manually in [YumaSdk.init].
 * The host app must grant location permissions before calling [YumaSdk.launchHome].
 */
internal class SdkLocationManager(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            _currentLocation.value = result.lastLocation
            Log.d(TAG, "Location updated: ${result.lastLocation?.latitude}, ${result.lastLocation?.longitude}")
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateIntervalMillis(3_000L)
            .build()

        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener {
            Log.e(TAG, "Failed to start location updates: ${it.message}")
        }

        // Also fetch last known location immediately
        fusedClient.lastLocation.addOnSuccessListener { location ->
            location?.let { _currentLocation.value = it }
        }
    }

    fun stopLocationUpdates() {
        fusedClient.removeLocationUpdates(locationCallback)
    }

    fun getLatestLocation(): Location? = _currentLocation.value
}
