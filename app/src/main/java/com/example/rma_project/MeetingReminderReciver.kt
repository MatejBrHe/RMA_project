package com.example.rma_project

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat

class MeetingReminderReceiver() : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        val meetingTitle = intent.getStringExtra("meeting_title")
            ?: "Upcoming meeting"
        val meetingHash = intent.getStringExtra("meeting_hash")
            ?: "Upcoming meeting".hashCode().toString()

        val channelId = meetingHash
        val channelName = meetingTitle
        val channel = NotificationChannel(channelId, channelName,
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, meetingHash)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Coffee meeting")
            .setContentText("Coffee meeting ${meetingTitle} starts soon.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(1, notification.build())
    }
}
