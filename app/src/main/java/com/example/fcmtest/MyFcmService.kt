package com.example.fcmtest

import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFcmService : FirebaseMessagingService() {
    companion object {
        const val TAG: String = "MyFcmService"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: " + token)
//        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle("FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}
