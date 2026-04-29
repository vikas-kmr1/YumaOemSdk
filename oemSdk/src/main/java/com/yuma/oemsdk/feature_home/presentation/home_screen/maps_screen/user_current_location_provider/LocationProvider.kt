package com.yumaoem.feature_home.presentation.home_screen.maps_screen.user_current_location_provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.yumaoem.core.utils.context.AndroidContextProvider
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class LocationProvider constructor(
    context: Any?
) {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context as Context)

    private val _currentLocation = MutableStateFlow<LatLong?>(null)
    val currentLocation: StateFlow<LatLong?> = _currentLocation

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    suspend fun startLocationUpdates() {
        if (_currentLocation.value == null) {
            getLastLocation(fusedLocationClient)
        }
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toSeconds(10)
            fastestInterval = TimeUnit.SECONDS.toSeconds(8)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val userLatLng =
                    locationResult.lastLocation?.let { LatLong(it.latitude, it.longitude) }
                _currentLocation.value = userLatLng
            }
        }

        if (AndroidContextProvider.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLastLocation(fusedLocationClient: FusedLocationProviderClient) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val userLatLng = LatLong(it.latitude, it.longitude)
                _currentLocation.value = userLatLng
            }
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Continuously fetches location until non-null, then returns it.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LatLong? {
        val lastKnown = getLastKnownLocation()
        if (lastKnown != null) {
            return LatLong(lastKnown.latitude, lastKnown.longitude)
        }

        return suspendCancellableCoroutine { cont ->
            val locationRequest = LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
                interval = 1000
                fastestInterval = 500
                numUpdates = 1
            }

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc: Location? = result.lastLocation
                    if (loc != null) {
                        val latLng = LatLong(loc.latitude, loc.longitude)
                        _currentLocation.value = latLng
                        fusedLocationClient.removeLocationUpdates(this)
                        if (cont.isActive) cont.resume(latLng)
                    }
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable && cont.isActive) {
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                null
            )

            cont.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(callback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { loc -> cont.resume(loc) }
            .addOnFailureListener { cont.resume(null) }
    }
}
