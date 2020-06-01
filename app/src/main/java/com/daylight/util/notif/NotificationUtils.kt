package com.daylight.util.notif

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.daylight.R
import com.daylight.habits.HabitsActivity


/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(title: String, messageBody: String, applicationContext: Context, actionIntents: List<Pair<String, PendingIntent>>, channel: String, id: Int) {
    // Create the content intent for the notification, which launches
    // this activity
    val contentIntent = Intent(applicationContext, HabitsActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        id,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        channel
    )
        .setSmallIcon(R.drawable.ic_habits_icon)
        .setContentTitle(title)

        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    if(id == 8) {
        builder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(messageBody))
    } else {
        builder
            .setContentText(messageBody)
    }

    actionIntents.forEach {
        builder.addAction(
            R.drawable.ic_add_icon,
            it.first,
            it.second
        )
    }

    notify(id, builder.build())
}

