package com.daylight.util.notif

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.daylight.R
import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitsDataSource
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.trackhabit.TrackHabitActivity
import com.daylight.util.AppExecutors

class AlarmReceiver: BroadcastReceiver() {

    private val REQUEST_CODE = 0
    private val FLAGS = 0

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != null) {
            val database = DaylightDatabase.getInstance(context)
            val habitsRepository = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

            // TODO Add more actions as ifs in line with this
            if (intent.action!!.equals(context.getString(R.string.action_notify_habit_reminder), ignoreCase = true)) {
                if (intent.extras != null) {
                    intent.getStringExtra("habitId")?.let {
                        habitsRepository.getHabit(it, object : HabitsDataSource.GetHabitCallback {
                            override fun onHabitLoaded(habit: Habit) {
                                val notificationManager = ContextCompat.getSystemService(
                                    context,
                                    NotificationManager::class.java
                                ) as NotificationManager

                                notificationManager.sendNotification(
                                    "Complete your habits!",
                                    "Remember to complete your habit - ${habit.title}!",
                                    context,
                                    emptyList()
                                )
                            }

                            override fun onDataNotAvailable() { }
                        })
                    }
                }
            }
            else if (intent.action!!.equals(context.getString(R.string.action_track_habit), ignoreCase = true)) {
                if (intent.extras != null) {
                    intent.getStringExtra("habitId")?.let {
                        habitsRepository.getHabit(it, object : HabitsDataSource.GetHabitCallback {
                            override fun onHabitLoaded(habit: Habit) {

                                // Create pending intents for the two action options
                                val trackFromNotificationIntent = Intent(context, TrackReceiver::class.java)
                                intent.putExtra("habitId", habit.id)
                                val trackFromNotificationPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    2,
                                    trackFromNotificationIntent,
                                    FLAGS)

                                val trackIntent = Intent(context, TrackHabitActivity::class.java)
                                val trackPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    3,
                                    trackIntent,
                                    FLAGS)

                                val actions = listOf(Pair("Yes - Track now!", trackFromNotificationPendingIntent),
                                Pair("Choose tracked time", trackPendingIntent))

                                val notificationManager = ContextCompat.getSystemService(
                                    context,
                                    NotificationManager::class.java
                                ) as NotificationManager

                                notificationManager.sendNotification(
                                    "Have you completed your habit?",
                                    "Have you completed the habit - ${habit.title}?",
                                    context,
                                    actions
                                )
                            }

                            override fun onDataNotAvailable() { }
                        })
                    }
                }
            }
        }
    }

}