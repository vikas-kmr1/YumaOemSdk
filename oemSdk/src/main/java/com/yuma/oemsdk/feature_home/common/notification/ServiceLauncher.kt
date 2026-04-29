package com.yumaoem.feature_home.common.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import com.yuma.oemsdk.feature_home.common.notification.NotificationService


class ServiceLauncher(
    private val context: Context
) {
    fun startDummyNotification() {
        Intent(context, NotificationService::class.java).also {
            it.action = NotificationService.MyForegroundServiceActions.START.toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }
    }

    fun sendForeGroundNotification(expireTime: String, stationName: String) {
        Intent(context, NotificationService::class.java).also {
            it.putExtra("ExpireTime", expireTime)
            it.putExtra("StationName", stationName)
            it.action =
                NotificationService.MyForegroundServiceActions.UPDATE.toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }
    }

    fun stopForeGroundNotification() {
        Intent(context, NotificationService::class.java).also {
            it.action = NotificationService.MyForegroundServiceActions.STOP.toString()
            context.startService(it)
        }
    }
}