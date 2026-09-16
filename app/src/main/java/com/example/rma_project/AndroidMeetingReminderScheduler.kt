package com.example.rma_project

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.time.LocalDateTime
import java.time.ZoneId

interface MeetingReminderScheduler {
    fun scheduleMeetingReminder(
        meetingTitle: String,
        meetingTime: LocalDateTime,
        userId: String
    )

    fun cancelMeetingReminder(
        meetingTitle: String,
        meetingTime: LocalDateTime,
        userId: String
    )
}

class AndroidMeetingReminderScheduler(
    private val context: Context
) : MeetingReminderScheduler {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun scheduleMeetingReminder(
        meetingTitle: String,
        meetingTime: LocalDateTime,
        userId: String
    ) {
        val meetingHash = "${meetingTitle}${meetingTime}${userId}".hashCode().toString()
        val meetingStartMillis = meetingTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val reminderTime = meetingStartMillis - 30 * 60 * 1000L

        if (reminderTime <= System.currentTimeMillis()) {
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
            return
        }

        val intent = Intent(context, MeetingReminderReceiver::class.java).apply {
            putExtra("meeting_hash", meetingHash)
            putExtra("meeting_title", meetingTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            meetingHash.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }

    override fun cancelMeetingReminder(
        meetingTitle: String,
        meetingTime: LocalDateTime,
        userId: String
    ) {
        val meetingHash = "${meetingTitle}${meetingTime}${userId}".hashCode().toString()

        val intent = Intent(
            context,
            MeetingReminderReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            meetingHash.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}