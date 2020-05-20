package com.example.daylight.util.notif

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.daylight.R
import com.example.daylight.habits.HabitsActivity

// Notification ID.
private val NOTIFICATION_ID = 0

/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(title: String, messageBody: String, applicationContext: Context, actionIntents: List<Pair<String, PendingIntent>>) {
    // Create the content intent for the notification, which launches
    // this activity
    val contentIntent = Intent(applicationContext, HabitsActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.habit_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_habits_icon)
        .setContentTitle(title)
        .setContentText(messageBody)

        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    actionIntents.forEach {
        builder.addAction(
            R.drawable.ic_add_icon,
            it.first,
            it.second
        )
    }

    notify(NOTIFICATION_ID, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
