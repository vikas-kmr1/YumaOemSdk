package com.yumaoem.core.utils.notifications.data.fire_base_messaging

import android.app.NotificationManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.onboarding.utils.FcmTokenHandlerImpl
import com.yumaoem.core.utils.app_utils.FcmTokenHandler
import com.yumaoem.core.utils.notifications.data.NotificationConstants
import com.yumaoem.core.utils.notifications.data.show_notification.NotificationImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FireBaseService : FirebaseMessagingService() {

    private var sender: FcmTokenHandler = FcmTokenHandlerImpl(
        YumaSdk.prefManager,
        YumaSdk.onboardingDatasource
    )

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            sender.sendFcmToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationImpl = NotificationImpl(applicationContext)
        try {
            val notification = message.notification
            if (notification != null) {
                val title = notification.title ?: NotificationConstants.NO_TITLE
                val body = notification.body ?: NotificationConstants.NO_BODY
                val imageUrl = notification.imageUrl ?: ""
                val channelId = NotificationConstants.CHANNEL_ID

                try {
                    notificationImpl.sendNotification(
                        title = title,
                        body = body,
                        priority = NotificationManager.IMPORTANCE_HIGH,
                        image = imageUrl.toString(),
                        channelId = channelId
                    )
                } catch (e: Exception) {
                    Log.e("FM", "Error sending notification: ${e.message}", e)
                }

            } else {
                Log.w("FM", "Notification payload was null")
            }

        } catch (e: Exception) {
            Log.e("FM", "Unhandled exception in onMessageReceived: ${e.message}", e)
        }
    }
}
