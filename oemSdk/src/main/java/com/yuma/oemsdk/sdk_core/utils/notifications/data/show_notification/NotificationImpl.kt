package com.yumaoem.core.utils.notifications.data.show_notification

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.yuma.oemsdk.R

import com.yumaoem.core.utils.notifications.data.NotificationConstants
import com.yumaoem.core.utils.notifications.domain.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NotificationImpl(private val context: Context) : Notification {

    companion object {
        private var notificationManager: NotificationManager? = null
        private const val NOTIFICATION_PERMISSION_CODE = 1001
    }

    override fun createChannel(
        channelId: String,
        channelName: String,
        notificationImportance: Int,
        channelDescription: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                notificationImportance
            ).apply {
                description = channelDescription
            }
            try {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.createNotificationChannel(channel)
            } catch (e: Exception) {
                Log.e("NotificationImpl", "Failed to create channel: ${e.message}", e)
            }
        }
    }

    override fun requestPermissionIfNeeded(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    override fun sendNotification(
        title: String?,
        body: String?,
        priority: Int,
        image: String?,
        channelId: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager?.getNotificationChannel(channelId)
            if (channel == null) {
                createChannel(
                    channelId = NotificationConstants.CHANNEL_ID,
                    channelName = NotificationConstants.CHANNEL_NAME,
                    notificationImportance = NotificationManager.IMPORTANCE_HIGH,
                    channelDescription = NotificationConstants.CHANNEL_DESCRIPTION
                )
            }
        } else {
            createChannel(
                channelId = NotificationConstants.CHANNEL_ID,
                channelName = NotificationConstants.CHANNEL_NAME,
                notificationImportance = NotificationManager.IMPORTANCE_HIGH,
                channelDescription = NotificationConstants.CHANNEL_DESCRIPTION,
            )

        }
        CoroutineScope(Dispatchers.IO).launch {
            val largeBitmap =
                image?.let { convertUrlToBitMap(it, NotificationConstants.BITMAP_SIZE_LARGE) }
            val bigBitmap =
                image?.let { convertUrlToBitMap(it, NotificationConstants.BITMAP_SIZE_BIG) }

            if (largeBitmap == null || bigBitmap == null) {
                Log.d("NotificationImpl", "Bitmap returned null")
            }
            withContext(Dispatchers.Main) {
                try {
                    val builder = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.yuma_logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setLargeIcon(largeBitmap)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    if (image != null) {
                        builder.setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(bigBitmap)
                                .bigLargeIcon(null as Bitmap?)
                        )
                    }
                    notificationManager?.notify(System.currentTimeMillis().toInt(), builder.build())
                } catch (e: Exception) {
                    Log.e("NotificationImpl", "Failed to send notification: ${e.message}", e)
                }
            }
        }
    }

    override suspend fun convertUrlToBitMap(url: String, sizeFlag: String): Bitmap? {
        val loader = ImageLoader.Builder(context)
            .build()

        val (width, height) = when (sizeFlag) {
            NotificationConstants.BITMAP_SIZE_LARGE -> 256 to 256
            NotificationConstants.BITMAP_SIZE_BIG -> 1024 to 512
            else -> 256 to 256
        }

        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .size(width, height)
            .build()



        return try {
            when (val result = loader.execute(request)) {
                is SuccessResult -> {
                    result.drawable.toBitmap()
                }

                is ErrorResult -> {
                    null
                }

                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

}