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
import java.util.*

class AlarmReceiver: BroadcastReceiver() {

    private val REQUEST_CODE = 0
    private val FLAGS = 0

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != null) {
            val database = DaylightDatabase.getInstance(context)
            val habitsRepository = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))
            val moodsRepository = MoodsRepository.getInstance(MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

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
                    override fun onCorrelationDataLoaded(moodAndHabitTracking: List<MoodAndHabitTracking>) { val habitFound = findCorrelations(moodAndHabitTracking)

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

    private fun findCorrelations(moodAndHabitTracking: List<MoodAndHabitTracking>) : String {
        val moods : MutableList<Pair<String?, Int?>> = mutableListOf()
        val moodTracking : MutableList<Pair<String?, Calendar>> = mutableListOf()
        val habits : MutableList<Pair<String?, String?>> = mutableListOf()
        val habitTracking : MutableList<Pair<Calendar, String?>> = mutableListOf()

        moodAndHabitTracking.forEach {
            if (it.moodName != null && it.score != null) {
                moods.add(Pair(it.moodName, it.score))
            } else if (it.name != null) {
                moodTracking.add(Pair(it.name, it.date))
            } else if (it.title != null && it.habitid != null) {
                habits.add(Pair(it.title, it.habitid))
            } else if (it.habitidFromTracking != null) {
                habitTracking.add(Pair(it.completionDateTime, it.habitidFromTracking))
            }
        }

        var totalMoodScore = 0
        val today = Calendar.getInstance()
        today.add(Calendar.HOUR, -24 * 30)

        moodTracking.forEach {
            // Only look at the last 30 days
            if (it.second.timeInMillis > today.timeInMillis) {
                moods.forEach { mood ->
                    if (mood.first == it.first) {
                        totalMoodScore += mood.second!!
                    }
                }
            }
        }

        // Average mood score across the time tracked
        if(moodTracking.count() != 0) {

            totalMoodScore /= moodTracking.count()
        }

        var prevBiggestDiff = 0
        var prevBiggestDiffHabitName = ""
        var prevBiggestDiffHabitId = ""
        var habitScore = 0
        var habitMoodOccurrenceCount = 0
        var habitMoodScore = 0
        var habitMoodDifference: Int

        habitTracking.forEach {habitTrack ->

            moodTracking.forEach { moodTrack ->
                if (moodTrack.second.get(Calendar.DAY_OF_YEAR) == habitTrack.first.get(Calendar.DAY_OF_YEAR) && moodTrack.second.get(Calendar.YEAR) == habitTrack.first.get(Calendar.YEAR)) {
                    habitMoodOccurrenceCount++
                    moods.forEach {
                        if(it.first == moodTrack.first) {
                            habitScore += it.second!!
                        }
                    }
                }
            }

            if(habitMoodOccurrenceCount != 0) {
                habitMoodScore = habitScore / habitMoodOccurrenceCount
            }

            habitMoodDifference = habitMoodScore - totalMoodScore

            if (habitMoodDifference > prevBiggestDiff) {
                prevBiggestDiff = habitMoodDifference
                prevBiggestDiffHabitId = habitTrack.second!!
            }

            habits.forEach {
                if(it.second == prevBiggestDiffHabitId) {
                    prevBiggestDiffHabitName = it.first.toString()
                }
            }
        }

        return if (prevBiggestDiff >= 0.5) {
            prevBiggestDiffHabitName
        } else {
            ""
        }
    }
}