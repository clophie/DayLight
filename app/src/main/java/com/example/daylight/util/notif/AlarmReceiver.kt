package com.example.daylight.util.notif

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.daylight.R
import com.example.daylight.data.source.Habit
import com.example.daylight.data.source.HabitsDataSource
import com.example.daylight.data.source.HabitsRepository
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.HabitsLocalDataSource
import com.example.daylight.util.AppExecutors

class AlarmReceiver: BroadcastReceiver() {

    private val TAG = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive() called with: context = [$context], intent = [$intent]")

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            "Sent from AlarmReceiver",
            context
        )

        /*if (intent.action != null) {
            // TODO Add more actions as ifs in line with this
            if (intent.action!!.equals(context.getString(R.string.action_notify_habit_reminder), ignoreCase = true)) {
                if (intent.extras != null) {
                    val database = DaylightDatabase.getInstance(context)
                    val habitsRepository = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

                    intent.extras!!.getString("habitId")?.let {
                        habitsRepository.getHabit(it, object : HabitsDataSource.GetHabitCallback {
                            override fun onHabitLoaded(habit: Habit) {
                                val notificationManager = ContextCompat.getSystemService(
                                    context,
                                    NotificationManager::class.java
                                ) as NotificationManager

                                notificationManager.sendNotification(
                                    "Remember to complete your habit - ${habit.title}!",
                                    context
                                )
                            }

                            override fun onDataNotAvailable() { }
                        })
                    }
                }
            }
        }*/
    }

}