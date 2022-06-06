package com.dcapps.spese.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dcapps.spese.R
import com.dcapps.spese.data.dto.notification.NotificationMessage
import com.dcapps.spese.enums.firebase.notification.NotificationDataFieldsEnum
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.views.MainActivity
import com.dcapps.spese.views.dialog.EditSpesaDialogFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TAG = "NotificationMessage"
private const val CHANNEL_ID = "spese"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class CustomFirebaseMessagingService : FirebaseMessagingService(){

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "New message received from: ${remoteMessage.from}")

        if(DBUtils.getLoggedUser().uid != remoteMessage.data[NotificationDataFieldsEnum.SENDER.value]){
            showNotification(remoteMessage)
        }
    }

    override fun onMessageSent(msgId: String) {
        Log.d(TAG, "Message sent: $msgId")
        super.onMessageSent(msgId)
    }

    companion object {
        fun sendNotification(notificationMessage: NotificationMessage) = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstanceService.api.postNotification(notificationMessage)
                if(!response.isSuccessful) {
                    Log.e(EditSpesaDialogFragment.TAG, response.errorBody().toString())
                }
            } catch(e: Exception) {
                Log.e(EditSpesaDialogFragment.TAG, e.toString())
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(remoteMessage: RemoteMessage) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(remoteMessage.data[NotificationDataFieldsEnum.TITLE.value])
            .setContentText(remoteMessage.data[NotificationDataFieldsEnum.BODY.value])
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "Notifiche"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = ""
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }



}