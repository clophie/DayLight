package com.example.daylight.data.source

import java.util.ArrayList
import java.util.LinkedHashMap


/**
 * Concrete implementation to load habits from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
class HabitsRepository(
    val habitsLocalDataSource: HabitsDataSource
) : HabitsDataSource {

    /**
     * This variable has public visibility so it can be accessed from tests.
     */
    var cachedHabits: LinkedHashMap<String, Habit> = LinkedHashMap()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var cacheIsDirty = false

    /**
     * Gets habits from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     *
     *
     * Note: [HabitsDataSource.LoadHabitsCallback.onDataNotAvailable] is fired if all data sources fail to
     * get the data.
     */
    override fun getHabits(callback: HabitsDataSource.LoadHabitsCallback) {
        // Respond immediately with cache if available and not dirty
        if (cachedHabits.isNotEmpty() && !cacheIsDirty) {
            callback.onHabitsLoaded(ArrayList(cachedHabits.values))
            return
        }

        if (cacheIsDirty) {
        } else {
            // Query the local storage if available. If not, query the network.
            habitsLocalDataSource.getHabits(object : HabitsDataSource.LoadHabitsCallback {
                override fun onHabitsLoaded(habits: List<Habit>) {
                    refreshCache(habits)
                    callback.onHabitsLoaded(ArrayList(cachedHabits.values))
                }

                override fun onDataNotAvailable() {
                }
            })
        }
    }

    override fun saveHabit(habit: Habit) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(habit) {
            habitsLocalDataSource.saveHabit(it)
        }
    }

    override fun completeHabit(habit: Habit) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(habit) {
            it.isCompleted = true
            habitsLocalDataSource.completeHabit(it)
        }
    }

    override fun completeHabit(habitId: String) {
        getHabitWithId(habitId)?.let {
            completeHabit(it)
        }
    }

    override fun activateHabit(habit: Habit) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(habit) {
            it.isCompleted = false
            habitsLocalDataSource.activateHabit(it)
        }
    }

    override fun activateHabit(habitId: String) {
        getHabitWithId(habitId)?.let {
            activateHabit(it)
        }
    }

    /**
     * Gets habits from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     *
     *
     * Note: [HabitsDataSource.GetHabitCallback.onDataNotAvailable] is fired if both data sources fail to
     * get the data.
     */
    override fun getHabit(habitId: String, callback: HabitsDataSource.GetHabitCallback) {
        val habitInCache = getHabitWithId(habitId)

        // Respond immediately with cache if available
        if (habitInCache != null) {
            callback.onHabitLoaded(habitInCache)
            return
        }

        // Load from server/persisted if needed.

        // Is the habit in the local data source? If not, query the network.
        habitsLocalDataSource.getHabit(habitId, object : HabitsDataSource.GetHabitCallback {
            override fun onHabitLoaded(habit: Habit) {
                // Do in memory cache update to keep the app UI up to date
                cacheAndPerform(habit) {
                    callback.onHabitLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
            }
        })
    }

    override fun refreshHabits() {
        cacheIsDirty = true
    }

    override fun deleteAllHabits() {
        habitsLocalDataSource.deleteAllHabits()
        cachedHabits.clear()
    }

    override fun deleteHabit(habitId: String) {
        habitsLocalDataSource.deleteHabit(habitId)
        cachedHabits.remove(habitId)
    }

    private fun refreshCache(habits: List<Habit>) {
        cachedHabits.clear()
        habits.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    private fun getHabitWithId(id: String) = cachedHabits[id]

    private inline fun cacheAndPerform(habit: Habit, perform: (Habit) -> Unit) {
        val cachedHabit = Habit(habit.title, habit.description, habit.id).apply {
            isCompleted = habit.isCompleted
        }
        cachedHabits.put(cachedHabit.id, cachedHabit)
        perform(cachedHabit)
    }

    companion object {

        private var INSTANCE: HabitsRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.

         * @param habitsRemoteDataSource the backend data source
         * *
         * @param habitsLocalDataSource  the device storage data source
         * *
         * @return the [HabitsRepository] instance
         */
        @JvmStatic fun getInstance(habitsLocalDataSource: HabitsDataSource): HabitsRepository {
            return INSTANCE ?: HabitsRepository(habitsLocalDataSource)
                .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}