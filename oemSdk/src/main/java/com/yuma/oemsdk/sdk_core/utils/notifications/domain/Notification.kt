package com.yumaoem.core.utils.notifications.domain

import android.app.Activity
import android.graphics.Bitmap

interface Notification {

    fun createChannel(
        channelId: String,
        channelName: String,
        notificationImportance: Int,
        channelDescription: String
    )

    fun requestPermissionIfNeeded(activity: Activity)

    fun sendNotification(
        title: String?,
        body: String?,
        priority: Int,
        image: String?,
        channelId : String
    )

    suspend fun convertUrlToBitMap(
        url: String,
        sizeFlag : String
    ): Bitmap?

}
