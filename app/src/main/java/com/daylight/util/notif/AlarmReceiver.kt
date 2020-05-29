package com.daylight.util.notif

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.daylight.R
import com.daylight.data.MoodAndHabitTracking
import com.daylight.data.MoodAndTracking
import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitsDataSource
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.data.moods.MoodsDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.trackhabit.TrackHabitActivity
import com.daylight.util.AppExecutors
import com.daylight.util.CorrelationProcessing
import java.util.*

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != null) {
            val database = DaylightDatabase.getInstance(context)
            val habitsRepository = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))
            val moodsRepository = MoodsRepository.getInstance(MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

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
                                    emptyList(),
                                    context.resources.getString(R.string.habit_notification_channel_id),
                                    0
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
                                trackFromNotificationIntent.putExtra("habitId", habit.id)
                                trackFromNotificationIntent.action = context.getString(R.string.action_track_habit)
                                val trackFromNotificationPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    2,
                                    trackFromNotificationIntent,
                                    FLAG_UPDATE_CURRENT)

                                // Create an Intent for the activity you want to start
                                val resultIntent = Intent(context, TrackHabitActivity::class.java)

                                // Create the TaskStackBuilder
                                val trackPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
                                    // Add the intent, which inflates the back stack
                                    addNextIntentWithParentStack(resultIntent)
                                    // Get the PendingIntent containing the entire back stack
                                    getPendingIntent(0, FLAG_UPDATE_CURRENT)
                                }

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
                                    actions as List<Pair<String, PendingIntent>>,
                                    context.resources.getString(R.string.habit_track_notification_channel_id),
                                    1
                                )
                            }

                            override fun onDataNotAvailable() { }
                        })
                    }
                }
            } else if (intent.action!!.equals(context.getString(R.string.action_mood_reminder), ignoreCase = true)) {
                moodsRepository.getMoodTracking(object : MoodsDataSource.GetMoodTrackingAndMoodCallback {
                    override fun onMoodTrackingLoaded(moodTracking: List<MoodAndTracking>) {
                        val actions = mutableListOf<Pair<String, PendingIntent>>()
                        var score2NotSet = true
                        var score3NotSet = true
                        var score4NotSet = true

                        moodTracking.forEach {
                            if (score2NotSet && it.score == 2) {
                                createMoodPendingIntent(actions, context, it)
                                score2NotSet = false
                            } else if (score3NotSet && it.score == 3) {
                                createMoodPendingIntent(actions, context, it)
                                score3NotSet = false
                            } else if (score4NotSet && it.score == 4) {
                                createMoodPendingIntent(actions, context, it)
                                score4NotSet = false
                            }
                        }

                        val notificationManager = ContextCompat.getSystemService(
                            context,
                            NotificationManager::class.java
                        ) as NotificationManager

                        notificationManager.sendNotification(
                            "How was today?",
                            "Do you want to track a mood for today?",
                            context,
                            actions as List<Pair<String, PendingIntent>>,
                            context.resources.getString(R.string.mood_notification_channel_id),
                            3
                        )
                    }

                    override fun onDataNotAvailable() { }
                })
            } else if (intent.action!!.equals(context.getString(R.string.action_correlation_alarm), ignoreCase = true)) {
                habitsRepository.getDataForCorrelationProcessing( object : HabitsDataSource.GetCorrelationDataCallback {
                    override fun onCorrelationDataLoaded(moodAndHabitTracking: List<MoodAndHabitTracking>) {
                        val habitFound = CorrelationProcessing.findCorrelations(moodAndHabitTracking)

                        if (habitFound.isNotEmpty()) {
                            val notificationManager = ContextCompat.getSystemService(
                                context,
                                NotificationManager::class.java
                            ) as NotificationManager

                            notificationManager.sendNotification(
                                "New Correlation Found!",
                                "Your mood tends to be more positive when you complete the habit - ${habitFound}, keep it up!",
                                context,
                                emptyList(),
                                context.resources.getString(R.string.correlation_notification_channel_id),
                                8
                            )
                        }
                    }

                    override fun onDataNotAvailable() { }
                })
            }
        }
    }

    private fun createMoodPendingIntent(actions: MutableList<Pair<String, PendingIntent>>, context: Context, mood: MoodAndTracking) {
        val trackFromNotificationIntent = Intent(context, TrackReceiver::class.java)
        trackFromNotificationIntent.putExtra("moodName", mood.moodName)
        trackFromNotificationIntent.action = context.getString(R.string.action_track_mood)
        val trackFromNotificationPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            mood.score * 10,
            trackFromNotificationIntent,
            FLAG_UPDATE_CURRENT)

        actions.add(Pair(mood.moodName, trackFromNotificationPendingIntent))
    }
}