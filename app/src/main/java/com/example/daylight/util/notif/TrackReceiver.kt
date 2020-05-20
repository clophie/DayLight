package com.example.daylight.util.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.daylight.R
import com.example.daylight.data.source.habits.HabitTracking
import com.example.daylight.data.source.habits.HabitsRepository
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.habits.HabitsLocalDataSource
import com.example.daylight.util.AppExecutors
import java.util.*

class TrackReceiver: BroadcastReceiver() {
    private val REQUEST_CODE = 0

    override fun onReceive(context: Context, intent: Intent) {
        val triggerTime = Calendar.getInstance()

        if (intent.action != null && intent.action!!.equals(context.getString(R.string.action_track_habit), ignoreCase = true) && intent.extras != null) {
            val database = DaylightDatabase.getInstance(context)
            val habitsRepository = HabitsRepository.getInstance(
                HabitsLocalDataSource.getInstance(
                    AppExecutors(), database.habitDao(), database.habitTrackingDao()))

            val habitTracking = intent.extras!!.getString("habitId")?.let {
                HabitTracking(
                    triggerTime,
                    it, triggerTime
                )
            }
            if (habitTracking != null) {
                habitsRepository.insertHabitTracking(habitTracking)
            }
        }
    }

}
