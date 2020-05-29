package com.daylight.util.notif

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.daylight.R
import com.daylight.data.habits.HabitTracking
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.data.moods.MoodTracking
import com.daylight.data.moods.MoodsRepository
import com.daylight.trackhabit.TrackHabitActivity
import com.daylight.util.AppExecutors
import java.util.*

class TrackReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val triggerTime = Calendar.getInstance()

        if (intent.action != null && intent.action!!.equals(context.getString(R.string.action_track_habit), ignoreCase = true) && intent.extras != null) {
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.cancel(1)

            val database = DaylightDatabase.getInstance(context)
            val habitsRepository = HabitsRepository.getInstance(
                HabitsLocalDataSource.getInstance(
                    AppExecutors(), database.habitDao(), database.habitTrackingDao()
                )
            )

            val habitTracking = intent.extras!!.getString("habitId")?.let {
                HabitTracking(
                    triggerTime,
                    it, triggerTime
                )
            }

            if (habitTracking != null) {
                habitsRepository.insertHabitTracking(habitTracking)
            }
        } else if (intent.action != null && intent.action!!.equals(context.getString(R.string.action_track_mood), ignoreCase = true) && intent.extras != null) {
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.cancel(3)

            val database = DaylightDatabase.getInstance(context)
            val moodsRepository = MoodsRepository.getInstance(
                MoodsLocalDataSource.getInstance(
                    AppExecutors(), database.moodDao(), database.moodTrackingDao()
                )
            )

            triggerTime.set(Calendar.MONTH, triggerTime.get(Calendar.MONTH) + 1)

            val moodTracking = intent.extras!!.getString("moodName")?.let {
                MoodTracking(
                    it,
                    triggerTime
                )
            }

            if (moodTracking != null) {
                moodsRepository.insertMoodTracking(moodTracking)
            }
        }
    }

}
