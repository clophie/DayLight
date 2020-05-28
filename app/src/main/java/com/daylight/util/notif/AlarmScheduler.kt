package com.daylight.util.notif

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.daylight.R
import com.daylight.data.habits.Habit
import java.util.*


/**
 * Helpers to assist in scheduling alarms for ReminderData.
 */
object AlarmScheduler {

    /**
     * Schedules all the alarms for [ReminderData].
     *
     * @param context      current application context
     * @param reminderData ReminderData to use for the alarm
     */
    fun scheduleAlarmsForHabit(context: Context, habit: Habit) {

        // get the AlarmManager reference
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule the alarms based on the days to complete the habit
        habit.days.forEach {
            // Create reminder alarm
            // Get the PendingIntent for the alarm
            val alarmIntent = createPendingIntent(context, habit, it.toString(), context.getString(R.string.action_notify_habit_reminder), 0)

            // Schedule the alarm
            scheduleAlarm(habit, getDayOfWeek(it), alarmIntent, alarmMgr, -15)

            // Create tracking alarm
            // Get the PendingIntent for the alarm
            val trackingAlarmIntent = createPendingIntent(context, habit, it.toString(), context.getString(R.string.action_track_habit), 1)

            // Schedule the alarm
            scheduleAlarm(habit, getDayOfWeek(it), trackingAlarmIntent, alarmMgr, 30)
        }
    }

    /**
     * Schedules a single alarm, with a delay in minutes. Negative delay = that many minutes behind chosen time
     */
    private fun scheduleAlarm(habit: Habit, dayOfWeek: Int, alarmIntent: PendingIntent?, alarmMgr: AlarmManager, delay: Int) {

        // Set up the time to schedule the alarm
        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        datetimeToAlarm.set(Calendar.HOUR_OF_DAY, habit.time.get(Calendar.HOUR_OF_DAY))
        datetimeToAlarm.set(Calendar.MINUTE, habit.time.get(Calendar.MINUTE))
        datetimeToAlarm.set(Calendar.SECOND, 0)
        datetimeToAlarm.set(Calendar.MILLISECOND, 0)
        datetimeToAlarm.set(Calendar.DAY_OF_WEEK, dayOfWeek)

        // Compare the datetimeToAlarm to today
        val today = Calendar.getInstance(Locale.getDefault())
        if (shouldNotifyToday(dayOfWeek, today, datetimeToAlarm)) {

            // Schedule for today
            alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                // Alarm is scheduled with a given delay
                datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7 + 1000 * 60 * delay).toLong(), alarmIntent)
            return
        }

        // Schedule 1 week out from the day
        datetimeToAlarm.roll(Calendar.WEEK_OF_YEAR, 1)
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            // Alarm is scheduled with a given delay
            datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7 + 1000 * 60 * delay).toLong(), alarmIntent)
    }

    /**
     * Creates a [PendingIntent] for the Alarm using the [ReminderData]
     *
     * @param context      current application context
     * @param reminderData ReminderData for the notification
     * @param day          String representation of the day
     */
    private fun createPendingIntent(context: Context, habit: Habit, day: String?, intentAction: String, requestCode: Int): PendingIntent? {
        // create the intent using a unique type
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = intentAction
            type = "$day-${habit.title}-${habit.description}-${intentAction}"
            putExtra("habitId", habit.id)
        }

        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Determines if the Alarm should be scheduled for today.
     *
     * @param dayOfWeek day of the week as an Int
     * @param today today's datetime
     * @param datetimeToAlarm Alarm's datetime
     */
    private fun shouldNotifyToday(dayOfWeek: Int, today: Calendar, datetimeToAlarm: Calendar): Boolean {
        return dayOfWeek == today.get(Calendar.DAY_OF_WEEK) &&
                today.get(Calendar.HOUR_OF_DAY) <= datetimeToAlarm.get(Calendar.HOUR_OF_DAY) &&
                today.get(Calendar.MINUTE) <= datetimeToAlarm.get(Calendar.MINUTE)
    }

    /**
     * Updates a notification.
     * Note: this just calls [AlarmScheduler.scheduleAlarmsForReminder] since
     * alarms with exact matching pending intents will update if they are already set, otherwise
     * call [AlarmScheduler.removeAlarmsForReminder] if the medicine has been administered.
     *
     * @param context      current application context
     * @param reminderData ReminderData for the notification
     */
    fun updateAlarmsForHabit(context: Context, habit: Habit) {
        if (habit.days != emptyArray<MaterialDayPicker.Weekday>()) {
            scheduleAlarmsForHabit(context, habit)
        } else {
            removeAlarmsForHabit(context, habit)
        }
    }

    /**
     * Removes the notification if it was previously scheduled.
     *
     * @param context      current application context
     * @param reminderData ReminderData for the notification
     */
    fun removeAlarmsForHabit(context: Context, habit: Habit) {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        intent.action = context.getString(R.string.action_notify_remove_notification)

        // type must be unique so Intent.filterEquals passes the check to make distinct PendingIntents
        // Schedule the alarms based on the days to administer the medicine
        if (habit.days == emptyArray<MaterialDayPicker.Weekday>()) {
            for (i in habit.days.indices) {
                val day = habit.days[i]

                // TODO uhhh fix this
                if (day != null) {
                    val type = String.format(Locale.getDefault(), "%s-%s-%s-%s", day, habit.title, habit.description)

                    intent.type = type
                    val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmMgr.cancel(alarmIntent)
                }
            }
        }
    }

    /**
     * Returns the int representation for the day of the week.
     *
     * @param days      array from resources
     * @param dayOfWeek String representation of the day e.g "Sunday"
     * @return [Calendar.DAY_OF_WEEK] for given dayOfWeek
     */
    private fun getDayOfWeek(dayOfWeek: MaterialDayPicker.Weekday): Int {
        return when (dayOfWeek) {
            MaterialDayPicker.Weekday.SUNDAY -> Calendar.SUNDAY
            MaterialDayPicker.Weekday.MONDAY -> Calendar.MONDAY
            MaterialDayPicker.Weekday.TUESDAY -> Calendar.TUESDAY
            MaterialDayPicker.Weekday.WEDNESDAY -> Calendar.WEDNESDAY
            MaterialDayPicker.Weekday.THURSDAY -> Calendar.THURSDAY
            MaterialDayPicker.Weekday.FRIDAY -> Calendar.FRIDAY
            else -> Calendar.SATURDAY
        }
    }

}