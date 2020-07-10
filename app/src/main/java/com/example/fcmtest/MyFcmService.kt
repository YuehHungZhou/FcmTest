package com.example.fcmtest

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFcmService : FirebaseMessagingService() {
    companion object {
        const val TAG: String = "MyFcmService"

        const val test_chennel_id = "test_channel_id"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: " + token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("MyFcmService", "~~~~~~~~~~~~~ start ~~~~~~~~~~~~~")

        Log.d("MyFcmService", "From : " + remoteMessage.from)

        Log.d("MyFcmService", "data : " + remoteMessage.data.toString())

        Log.d("MyFcmService", "notification title : " + remoteMessage.notification?.title)

        Log.d("MyFcmService", "notification body : " + remoteMessage.notification?.body)

        Log.d("MyFcmService", "~~~~~~~~~~~~~  end  ~~~~~~~~~~~~~")

        sendNotification(getMessageBody(remoteMessage))

    }

    private fun getMessageBody(remoteMessage: RemoteMessage): String {

        return if (remoteMessage.notification != null) {
            remoteMessage.notification?.body.toString()
        } else if (remoteMessage.data.isNotEmpty()) {
            remoteMessage.data["body"].toString()
        } else {
            ""
        }
    }

    private fun sendNotification(messageBody: String) {

        if (messageBody.isEmpty()) {
            return
        }

        val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mChannel: NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mChannel = NotificationChannel(
                test_chennel_id,
                "test_channel_name",
                NotificationManager.IMPORTANCE_HIGH
            )

            mChannel.lightColor = Color.GRAY

            mChannel.enableLights(true)

            mChannel.description = "test_channel_description"

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            mChannel.setSound(defaultSoundUri, audioAttributes)

            mNotificationManager.createNotificationChannel(mChannel)
        }


        val notificationBuilder = NotificationCompat.Builder(this, test_chennel_id)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.mipmap.sym_def_app_icon
                )
            )
            .setTicker("FCM Ticker")
            .setContentTitle("FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setLights(0xff0000ff.toInt(), 300, 1000)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setSound(defaultSoundUri);
        }

        mNotificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}
