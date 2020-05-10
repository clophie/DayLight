package com.example.daylight.data.source.local

import androidx.annotation.VisibleForTesting
import com.example.daylight.data.source.Habit
import com.example.daylight.data.source.HabitTracking
import com.example.daylight.data.source.HabitsDataSource
import com.example.daylight.util.AppExecutors
import java.util.*

/**
 * Concrete implementation of a data source as a db.
 */
class HabitsLocalDataSource private constructor(
    val appExecutors: AppExecutors,
    val habitsDao: HabitsDao,
    val habitTrackingDao: HabitTrackingDao
) : HabitsDataSource {

    /**
     * Note: [HabitsDataSource.LoadHabitsCallback.onDataNotAvailable] is fired if the database doesn't exist
     * or the table is empty.
     */
    override fun getHabits(callback: HabitsDataSource.LoadHabitsCallback) {
        appExecutors.diskIO.execute {
            val habits = habitsDao.getHabits()
            appExecutors.mainThread.execute {
                if (habits.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable()
                } else {
                    callback.onHabitsLoaded(habits)
                }
            }
        }
    }

    /**
     * Note: [HabitsDataSource.GetHabitCallback.onDataNotAvailable] is fired if the [Habit] isn't
     * found.
     */
    override fun getHabit(habitId: String, callback: HabitsDataSource.GetHabitCallback) {
        appExecutors.diskIO.execute {
            val habit = habitsDao.getHabitById(habitId)
            appExecutors.mainThread.execute {
                if (habit != null) {
                    callback.onHabitLoaded(habit)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun saveHabit(habit: Habit) {
        appExecutors.diskIO.execute { habitsDao.insertHabit(habit) }
    }

    override fun completeHabit(habit: Habit) {
        appExecutors.diskIO.execute { habitsDao.updateCompleted(habit.id, true) }
    }

    override fun activateHabit(habit: Habit) {
        appExecutors.diskIO.execute { habitsDao.updateCompleted(habit.id, false) }
    }

    override fun refreshHabits() {
        // Not required because the {@link HabitsRepository} handles the logic of refreshing the
        // habits from all the available data sources.
    }

    override fun deleteAllHabits() {
        appExecutors.diskIO.execute { habitsDao.deleteHabits() }
    }

    override fun deleteHabit(habitId: String) {
        appExecutors.diskIO.execute { habitsDao.deleteHabitById(habitId) }
    }

    override fun getHabitTracking() {
        appExecutors.diskIO.execute { habitTrackingDao.getHabitTracking() }
    }

    override fun getHabitTrackingByHabitId(habitId: String, callback: HabitsDataSource.GetHabitTrackingCallback) {
        appExecutors.diskIO.execute {
            val habitTracking = habitTrackingDao.getHabitTrackingByHabitId(habitId)
            appExecutors.mainThread.execute {
                if (habitTracking != null) {
                    callback.onHabitTrackingLoaded(habitTracking)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun insertHabitTracking(habitTracking: HabitTracking) {
        appExecutors.diskIO.execute { habitTrackingDao.insertHabitTracking(habitTracking) }
    }

    override fun updateHabitTracking(habitTracking: HabitTracking) {
        appExecutors.diskIO.execute { habitTrackingDao.updateHabitTracking(habitTracking) }
    }

    override fun deleteHabitTrackingByHabitId(habitId: String) {
        appExecutors.diskIO.execute { habitTrackingDao.deleteHabitTrackingByHabitId(habitId) }
    }

    override fun deleteHabitTrackingByTimestamp(timestamp: Calendar) {
        appExecutors.diskIO.execute { habitTrackingDao.deleteHabitTrackingByTimestamp(timestamp) }
    }

    override fun deleteAllHabitTracking() {
        appExecutors.diskIO.execute { habitTrackingDao.deleteHabitTracking() }
    }

    companion object {
        private var INSTANCE: HabitsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, habitsDao: HabitsDao, habitTrackingDao: HabitTrackingDao): HabitsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(HabitsLocalDataSource::javaClass) {
                    INSTANCE = HabitsLocalDataSource(appExecutors, habitsDao, habitTrackingDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}