package com.yuma.oemsdk.feature_home.common.notification


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.toColorInt
import com.yuma.oemsdk.R
import com.yuma.oemsdk.sdk_core.utils.ComposeAppMainActivityProvider
import com.yuma.oemsdk.sdk_core.utils.MainActivityProvider
import com.yumaoem.core.utils.context.AndroidContextProvider.context
import com.yumaoem.feature_home.domain.usecase.checkin_screen.ObserveTokenExpiryCountdownUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class NotificationService : Service() {

    private val CHANNEL_ID = "BookingUpdates"
    private val NOTIFICATION_ID = 1
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    private val observeTokenExpiryCountdownUseCase: ObserveTokenExpiryCountdownUseCase  = ObserveTokenExpiryCountdownUseCase
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val isDarkTheme by lazy {
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            // Handle service restart by system
            tempStart()
            return START_STICKY
        }
        val action = intent.action
        val time = intent.getStringExtra("ExpireTime")
        val stationName = intent.getStringExtra("StationName")
        when (action) {
            MyForegroundServiceActions.START.toString() -> {
                tempStart()
            }
            MyForegroundServiceActions.UPDATE.toString() -> start(
                time.toString(),
                stationName.toString()
            )

            MyForegroundServiceActions.STOP.toString() -> stopSelf()
        }

        return START_STICKY
    }
    private fun tempStart(){
        val manager = getSystemService(NotificationManager::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = manager?.getNotificationChannel(CHANNEL_ID)
            if(channel == null){
                createNotificationChannel()
            }
        }else{
            createNotificationChannel()
        }
        val notification = tempNotification().build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun tempNotification() : NotificationCompat.Builder{
        val notificationIntent = Intent(context, mainAct.getMainActivityClass()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.yuma_logo)
            .setContentTitle("Token Booking started")
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
    }

    private fun start(time: String, stationName: String) {
        startTimer(time, stationName)
    }

    private fun startTimer(time: String, stationName: String) {
        serviceScope.launch {
            observeTokenExpiryCountdownUseCase(time.toLong()).collect { value ->
                updateNotification(value, stationName)
            }
            stopSelf()
        }
    }

    private val mainAct: MainActivityProvider = ComposeAppMainActivityProvider
    @SuppressLint("RemoteViewLayout", "LaunchActivityFromNotification")
    private fun createNotification(time: String, stationName: String): NotificationCompat.Builder {
        val notificationIntent = Intent(context, mainAct.getMainActivityClass()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val remoteViews = RemoteViews(packageName, R.layout.notification_layout_large)
        val remoteViewsSmall = RemoteViews(packageName, R.layout.notification_layout_small)

        val titleColor = if (isDarkTheme) { Color.WHITE } else { Color.BLACK }
        val subTitleColor = if (isDarkTheme) { Color.WHITE } else { "#4B4E52".toColorInt() }
        val tokenColor = if (isDarkTheme) { Color.WHITE } else { "#0066DA".toColorInt() }

        remoteViews.setTextColor(R.id.titleText, titleColor)
        remoteViews.setTextColor(R.id.subTitleText, subTitleColor)
        remoteViews.setTextColor(R.id.tokenLabel, subTitleColor)
        remoteViews.setTextViewText(R.id.tokenTime, time)
        remoteViews.setTextColor(R.id.tokenTime, tokenColor)

        remoteViewsSmall.setTextColor(R.id.titleTextSmall, titleColor)
        remoteViewsSmall.setTextViewText(R.id.timerTextSmall, time)
        remoteViewsSmall.setTextColor(R.id.timerTextSmall, tokenColor)

        remoteViews.setTextViewText(R.id.titleText, "Reach $stationName station")

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.yuma_logo)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViewsSmall)
            .setCustomBigContentView(remoteViews)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
    }

    private fun updateNotification(time: String, stationName: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(time, stationName).build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "TokenBookingUpdates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows token booking updates"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    enum class MyForegroundServiceActions {
        START, STOP, UPDATE
    }
}