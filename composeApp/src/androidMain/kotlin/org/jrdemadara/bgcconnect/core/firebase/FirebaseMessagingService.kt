package org.jrdemadara.bgcconnect.core.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.jrdemadara.bgcconnect.MainActivity
import org.jrdemadara.bgcconnect.R
import org.jrdemadara.bgcconnect.data.FirebaseMessageRequest
import org.jrdemadara.bgcconnect.data.FirebaseMessageRequestData
import kotlin.random.Random

class FirebaseMessagingService : FirebaseMessagingService() {
    private val firebaseManager = FirebaseEventManager()
    private val settings: Settings = Settings()
    private val random = Random

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        val firebaseData = FirebaseMessageRequest(
            title = remoteMessage.notification?.title,
            body = remoteMessage.notification?.body,
            data = FirebaseMessageRequestData(
                id = data["id"].toString().toInt(),
                lastname = data["lastname"].toString(),
                firstname = data["firstname"].toString(),
                avatar = data["avatar"].toString(),
                status = data["status"].toString(),
                sender = data["sender_id"].toString().toInt(),
                requested_at = data["requested_at"].toString()
            )
        )

        firebaseManager.onMessageReceived(firebaseData)

        remoteMessage.notification?.let { message ->
            sendNotification(message)
        }
    }

    private fun sendNotification(message: RemoteMessage.Notification) {
        // If you want the notifications to appear when your app is in foreground

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_IMMUTABLE
        )

        val channelId = this.getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, CHANNEL_NAME, IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        manager.notify(random.nextInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        Log.d("FCM","New token: $token")
        settings["fcm_token"] = token
    }


    companion object {
        const val CHANNEL_NAME = "FCM notification channel"
    }
}