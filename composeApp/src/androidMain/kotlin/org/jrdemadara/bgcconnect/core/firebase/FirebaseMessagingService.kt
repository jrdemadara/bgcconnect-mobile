package org.jrdemadara.bgcconnect.core.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jrdemadara.bgcconnect.MainActivity
import org.jrdemadara.bgcconnect.R
import org.koin.core.context.GlobalContext
import kotlin.random.Random

class FirebaseMessagingService : FirebaseMessagingService() {

    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Channel creation for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for message notifications"
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        manager.notify(Random.nextInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        val firebaseManager = GlobalContext.get().get<FirebaseManager>()
        firebaseManager.onNewToken(token)
        Log.d("FCM","New token: $token")

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val firebaseManager = GlobalContext.get().get<FirebaseManager>()
        firebaseManager.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val title = data["title"].toString()
        val body = data["body"].toString()
        sendNotification(title, body)
    }

    companion object {
        const val CHANNEL_NAME = "FCM notification channel"
    }
}