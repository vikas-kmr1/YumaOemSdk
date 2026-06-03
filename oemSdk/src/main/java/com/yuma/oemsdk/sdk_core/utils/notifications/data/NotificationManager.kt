package com.yumaoem.core.utils.notifications.data

import android.app.NotificationManager
import com.yumaoem.core.utils.context.AndroidContextProvider
import com.yumaoem.core.utils.notifications.data.show_notification.NotificationImpl
import com.yumaoem.core.utils.notifications.domain.Notification


object NotificationManager {

    private val notification: Notification = NotificationImpl(AndroidContextProvider.context!!)


    fun setUpChannel() {
        notification.createChannel(
            channelId = NotificationConstants.CHANNEL_ID,
            channelName = NotificationConstants.CHANNEL_NAME,
            notificationImportance = NotificationManager.IMPORTANCE_DEFAULT,
            channelDescription = NotificationConstants.CHANNEL_DESCRIPTION
        )
    }
}