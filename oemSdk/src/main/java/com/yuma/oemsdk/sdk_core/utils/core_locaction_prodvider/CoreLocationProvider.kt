package com.yumaoem.core.utils.core_locaction_prodvider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.yumaoem.core.utils.context.AndroidContextProvider.context
import com.yumaoem.core.utils.context.PlatformContext
import com.yumaoem.core.utils.core_locaction_prodvider.model.CoreLatLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class CoreLocationProvider  constructor(
    context: Any?
) {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context as Context)

    private val _currentLocation = MutableStateFlow<CoreLatLong?>(null)
  val currentLocation: StateFlow<CoreLatLong?> = _currentLocation

    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
  suspend fun startLocationUpdates() {
        val locationRequest: LocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(30)
        ).apply {
            setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(30))
        }.build()

        if (_currentLocation.value == null) {
            getLastLocation(fusedLocationClient)
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val userLatLng =
                    locationResult.lastLocation?.let { CoreLatLong(it.latitude, it.longitude) }
                _currentLocation.value = userLatLng
            }
        }

        if (context?.let {
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
                val userLatLng = CoreLatLong(it.latitude, it.longitude)
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
  suspend fun getCurrentLocation(): CoreLatLong? = withContext(Dispatchers.Main) {
        if (ContextCompat.checkSelfPermission(PlatformContext.getApplicationContext() as Context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return@withContext null
        }

        val locationRequest: LocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(10)
        ).apply {
            setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(5))
            setMaxUpdates(1)
        }.build()

        try {
            if (fusedLocationClient.lastLocation.isComplete) {
                fusedLocationClient.lastLocation.result?.let {
                    return@withContext CoreLatLong(it.latitude, it.longitude)
                }
            }
        } catch (e: Exception) {
            // Ignore and fall through to requestLocationUpdates
        }

        withTimeout(30_000) {
            suspendCancellableCoroutine<CoreLatLong?> { cont ->
                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { loc ->
                            if (cont.isActive) {
                                val latLng = CoreLatLong(loc.latitude, loc.longitude)
                                fusedLocationClient.removeLocationUpdates(this)
                                cont.resume(latLng)
                            }
                        }
                    }

                    override fun onLocationAvailability(availability: LocationAvailability) {}
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )

                cont.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(callback)
                }
            }
        }
    }
}
