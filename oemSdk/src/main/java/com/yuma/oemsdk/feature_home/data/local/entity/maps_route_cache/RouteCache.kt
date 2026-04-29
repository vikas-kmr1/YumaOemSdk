package com.yumaoem.feature_home.data.local.entity.maps_route_cache

import com.yumaoem.core.utils.map_style.calculateDistanceInMeters
import com.yumaoem.feature_home.domain.model.maps.stationRoute.RouteInfo
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong

data class CachedRoute(
    val origin: LatLong,
    val routeInfo: RouteInfo
)

class RouteCache(private val thresholdMeters: Double = 20.0) {

    private val cache: MutableMap<LatLong, CachedRoute> = mutableMapOf()

    fun getIfValid(origin: LatLong, destination: LatLong): RouteInfo? {
        val cached = cache[destination] ?: return null
        val distanceFromCachedOrigin = calculateDistanceInMeters(
             startLat =  origin.latitude,
             startLng = origin.longitude,
             endLat = cached.origin.latitude,
             endLng =  cached.origin.longitude
           )

        return if (distanceFromCachedOrigin <= thresholdMeters) {
            cached.routeInfo
        } else {
            null
        }
    }

    fun save(origin: LatLong, destination: LatLong, routeInfo: RouteInfo) {
        cache[destination] = CachedRoute(origin, routeInfo)
    }

    fun clear() {
        cache.clear()
    }
}
