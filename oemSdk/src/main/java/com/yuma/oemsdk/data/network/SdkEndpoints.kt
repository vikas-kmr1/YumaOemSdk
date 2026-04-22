package com.yuma.oemsdk.data.network

/**
 * All API endpoints used by the SDK's internal network layer.
 * Mirrors [Endpoints] from the OEM app's feature-home module.
 */
internal object SdkEndpoints {

    // ── Token / Swap Flow ─────────────────────────────────────────────────────
    const val BOOK_TOKEN              = "token/book"
    const val NEARBY_STATIONS         = "charging-stations/nearby/operation-status"
    const val STATION_OPERATION_STATUS = "charging-stations/operation-status"
    const val BEACON_V2               = "charging-station-beacons/v3/nearby"
    const val CHECK_IN_AT_STATION     = "token"               // PUT /{tokenId}/check-in
    const val CANCEL_TOKEN            = "token"               // PUT /{tokenId}/cancel
    const val TOKEN_STATUS            = "token/token-status"
    const val DROP_OFF_SCREEN         = "token/screen"
    const val VALIDATE_LOCATION       = "token/validate-location"
    const val START_DIY_SWAP          = "yuzen/oem/diy-swap-details"
    const val TAG_BATTERY             = "yuzen/oem/map-new-batteries-on-bike"

    fun REVERT_TOKEN_CHECK_IN(tokenId: Int) = "token/$tokenId/revert-to-active"

    // ── Profile / User ────────────────────────────────────────────────────────
    const val GET_CURRENT_BATTERY_DETAILS = "batteries/client-vehicle"
    const val GET_SWAP_HISTORY            = "vehicle-battery-swap-logs/v2/history"
    const val LOGOUT_USER                 = "auth/logout"
    const val REMOVE_FCM_TOKEN            = "push-notification/remove-fcm-token"
    const val WHATSAPP_HELP               = "token/whatsapp/help"

    // ── Payments ──────────────────────────────────────────────────────────────
    const val GET_ALL_PLANS   = "plans/get-all-plans"
    const val CREATE_ORDER    = "plans/buy-plan"
    const val PAYMENT_STATUS  = "plans/payment/status"

    // ── Maps / Directions (external) ──────────────────────────────────────────
    const val ROUTE_BY_ROAD = "https://maps.googleapis.com/maps/api/directions/json"

    // ── Misc ──────────────────────────────────────────────────────────────────
    const val MAKE_CALL = "customer-support/make-call"

    // ── Auth (Silent authentication endpoint) ─────────────────────────────────
    // TODO: Replace with actual endpoint provided by Yuma backend team
    const val CLIENT_AUTH = "api/v1/oem/auth/client-token"
}
