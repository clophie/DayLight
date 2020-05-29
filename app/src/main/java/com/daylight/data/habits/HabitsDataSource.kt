package com.daylight.data.habits

import com.daylight.data.HabitAndTracking
import com.daylight.data.MoodAndHabitTracking
import java.util.*

/**
 * Main entry point for accessing tasks data.
 *
 *
 * For simplicity, only getHabits() and getHabit() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */

interface HabitsDataSource {

    interface LoadHabitsCallback {

        fun onHabitsLoaded(habits: List<Habit>)

        fun onDataNotAvailable()
    }

    interface GetHabitCallback {

        fun onHabitLoaded(habit: Habit)

        fun onDataNotAvailable()
    }

    interface GetHabitTrackingCallback {

        fun onHabitTrackingLoaded(habitTracking: List<HabitTracking>)

        fun onDataNotAvailable()
    }

    interface GetHabitAndTrackingCallback {

        fun onHabitTrackingLoaded(habitTracking: List<HabitAndTracking>)

        fun onDataNotAvailable()
    }

    interface GetCorrelationDataCallback {

        fun onCorrelationDataLoaded(moodAndHabitTracking: List<MoodAndHabitTracking>)

        fun onDataNotAvailable()
    }

    fun getHabits(callback: LoadHabitsCallback)

    fun getHabit(habitId: String, callback: GetHabitCallback)

    fun saveHabit(habit: Habit)

    fun refreshHabits()

    fun deleteAllHabits()

    fun deleteHabit(habitId: String)

    fun getHabitTracking(callback: GetHabitAndTrackingCallback)

    fun getHabitTrackingByHabitId(habitId: String, callback: GetHabitTrackingCallback)

    fun insertHabitTracking(habitTracking: HabitTracking)

    fun updateHabitTracking(habitTracking: HabitTracking)

    fun deleteHabitTrackingByHabitId(habitId: String)

    fun deleteHabitTrackingByTimestamp(timestamp: Calendar)

    fun deleteAllHabitTracking()

    fun getDataForCorrelationProcessing(callback: GetCorrelationDataCallback)
}