package com.yumaoem.feature_home.data.network.util

object Endpoints {

    const val BOOK_TOKEN = "token/book"

    const val ALL_CHARGING_STATIONS = "charging-stations/cs/nearby"

    const val NEARBY_STATIONS = "charging-stations/nearby/operation-status"

    const val ROUTE_BY_ROAD = "https://maps.googleapis.com/maps/api/directions/json"

    const val STATION_OPERATION_STATUS = "charging-stations/operation-status"

    const val BEACON_DETAILS = "charging-station-beacons/cs"

    const val BEACON_V2 = "charging-station-beacons/v3/nearby"

    const val CHECK_IN_AT_STATION = "token"

    const val CANCEL_TOKEN = "token"

    const val TOKEN_STATUS = "token/token-status"

    const val DROP_OFF_SCREEN = "token/screen"

    const val GET_CURRENT_BATTERY_DETAILS = "batteries/client-vehicle"


    const val GET_SWAP_HISTORY = "vehicle-battery-swap-logs/v2/history"

    const val WHATSAPP_HELP = "token/whatsapp/help"

    const val REMOVE_FCM_TOKEN = "push-notification/remove-fcm-token"

    const val START_DIY_SWAP = "yuzen/oem/diy-swap-details"

    const val YUZEN_DEV_BASE_URL = "https://dev-backend-api.yumax.app/"

    const val VALIDATE_LOCATION = "token/validate-location"

    const val LOGOUT_USER = "auth/logout"

    fun REVERT_TOKEN_CHECK_IN(tokenId: Int) = "token/$tokenId/revert-to-active"

    const val MAKE_CALL = "customer-support/make-call"

    const val GET_ALL_PLANS = "plans/get-all-plans"

    const val PAYMENT_STATUS = "plans/payment/status"

    const val CREATE_ORDER = "plans/buy-plan"

    const val TAG_BATTERY = "yuzen/oem/map-new-batteries-on-bike"
}

object DummyEndpoints {
    const val BOOK_TOKEN = "token/dummy/book"

    const val NEARBY_STATIONS = "charging-stations/cs/nearby/status/dummy"

    const val BEACON_DETAILS = "charging-station-beacons/cs/dummy"

    const val CHECK_IN_AT_STATION = "token/dummy"

    const val CANCEL_TOKEN = "token/dummy"
}