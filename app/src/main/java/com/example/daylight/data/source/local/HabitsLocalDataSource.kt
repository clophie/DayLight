package com.example.daylight.data.source.local

import androidx.annotation.VisibleForTesting
import com.example.daylight.data.source.Habit
import com.example.daylight.data.source.HabitsDataSource
import com.example.daylight.util.AppExecutors

/**
 * Concrete implementation of a data source as a db.
 */
class HabitsLocalDataSource private constructor(
    val appExecutors: AppExecutors,
    val habitsDao: HabitsDao
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

    override fun completeHabit(habitId: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateHabit(habit: Habit) {
        appExecutors.diskIO.execute { habitsDao.updateCompleted(habit.id, false) }
    }

    override fun activateHabit(habitId: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun refreshHabits() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllHabits() {
        appExecutors.diskIO.execute { habitsDao.deleteHabits() }
    }

    override fun deleteHabit(habitId: String) {
        appExecutors.diskIO.execute { habitsDao.deleteHabitById(habitId) }
    }

    companion object {
        private var INSTANCE: HabitsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, habitsDao: HabitsDao): HabitsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(HabitsLocalDataSource::javaClass) {
                    INSTANCE = HabitsLocalDataSource(appExecutors, habitsDao)
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