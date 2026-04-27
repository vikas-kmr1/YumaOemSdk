package com.yumaoem.core.utils.app_utils

interface FcmTokenHandler {
   suspend fun sendFcmToken(token: String)
   suspend fun insertFcmToken()
}