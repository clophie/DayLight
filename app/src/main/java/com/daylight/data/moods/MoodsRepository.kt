package com.daylight.data.moods

import com.daylight.data.MoodAndTracking
import java.util.*


/**
 * Concrete implementation to load moods from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
class MoodsRepository(
    val moodsLocalDataSource: MoodsDataSource
) : MoodsDataSource {

    /**
     * This variable has public visibility so it can be accessed from tests.
     */
    var cachedMoods: LinkedHashMap<String, Mood> = LinkedHashMap()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var cacheIsDirty = false

    /**
     * Gets moods from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     *
     *
     * Note: [MoodsDataSource.LoadMoodsCallback.onDataNotAvailable] is fired if all data sources fail to
     * get the data.
     */
    override fun getMoods(callback: MoodsDataSource.LoadMoodsCallback) {
        // Respond immediately with cache if available and not dirty
        if (cachedMoods.isNotEmpty() && !cacheIsDirty) {
            callback.onMoodsLoaded(ArrayList(cachedMoods.values))
            return
        }

        if (cacheIsDirty) {
            moodsLocalDataSource.getMoods(object : MoodsDataSource.LoadMoodsCallback {
                override fun onMoodsLoaded(moods: List<Mood>) {
                    refreshCache(moods)
                    callback.onMoodsLoaded(ArrayList(cachedMoods.values))
                }

                override fun onDataNotAvailable() {
                }
            })
        }
    }

    override fun refreshMoods() {
        cacheIsDirty = true
    }

    override fun saveMood(mood: Mood) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(mood) {
            moodsLocalDataSource.saveMood(it)
        }
    }

    /**
     * Gets moods from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     *
     *
     * Note: [MoodsDataSource.GetMoodCallback.onDataNotAvailable] is fired if both data sources fail to
     * get the data.
     */
    override fun getMood(moodId: String, callback: MoodsDataSource.GetMoodCallback) {
        val moodInCache = getMoodWithId(moodId)

        // Respond immediately with cache if available
        if (moodInCache != null) {
            callback.onMoodLoaded(moodInCache)
            return
        }

        // Load from server/persisted if needed.

        // Is the mood in the local data source? If not, query the network.
        moodsLocalDataSource.getMood(moodId, object : MoodsDataSource.GetMoodCallback {
            override fun onMoodLoaded(mood: Mood) {
                // Do in memory cache update to keep the app UI up to date
                cacheAndPerform(mood) {
                    callback.onMoodLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
            }
        })
    }

    override fun deleteAllMoods() {
        moodsLocalDataSource.deleteAllMoods()
        cachedMoods.clear()
    }

    override fun deleteMood(moodName: String) {
        moodsLocalDataSource.deleteMood(moodName)
        cachedMoods.remove(moodName)
    }

    override fun getMoodTracking(callback: MoodsDataSource.GetMoodTrackingAndMoodCallback) {
        moodsLocalDataSource.getMoodTracking(object : MoodsDataSource.GetMoodTrackingAndMoodCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodAndTracking>) {
                callback.onMoodTrackingLoaded(moodTracking)
            }

            override fun onDataNotAvailable() {
            }
        })
    }

    override fun getMoodTrackingByName(name: String, callback: MoodsDataSource.GetMoodTrackingCallback) {
        moodsLocalDataSource.getMoodTrackingByName(name, object : MoodsDataSource.GetMoodTrackingCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodTracking>) {
                callback.onMoodTrackingLoaded(moodTracking)
            }

            override fun onDataNotAvailable() {
            }
        })
    }

    override fun insertMoodTracking(moodTracking: MoodTracking) {
        // Set all time related fields to 0 so that any conflicts in date will be picked up
        moodTracking.date.set(Calendar.HOUR_OF_DAY, 0)
        moodTracking.date.set(Calendar.MINUTE, 0)
        moodTracking.date.set(Calendar.SECOND, 0)
        moodTracking.date.set(Calendar.MILLISECOND, 0)
        moodsLocalDataSource.insertMoodTracking(moodTracking)
    }

    override fun updateMoodTracking(moodTracking: MoodTracking) {
        moodsLocalDataSource.updateMoodTracking(moodTracking)
    }

    override fun deleteMoodTrackingByName(name: String) {
        moodsLocalDataSource.deleteMoodTrackingByName(name)
    }

    override fun deleteMoodTrackingByTimestamp(timestamp: Calendar) {
        moodsLocalDataSource.deleteMoodTrackingByTimestamp(timestamp)
    }

    override fun deleteAllMoodTracking() {
        moodsLocalDataSource.deleteAllMoodTracking()
    }

    private fun refreshCache(moods: List<Mood>) {
        cachedMoods.clear()
        moods.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    private fun getMoodWithId(id: String) = cachedMoods[id]

    private inline fun cacheAndPerform(mood: Mood, perform: (Mood) -> Unit) {
        val cachedMood = Mood(
            mood.score,
            mood.name
        )
        cachedMoods.put(cachedMood.id, cachedMood)
        perform(cachedMood)
    }

    companion object {

        private var INSTANCE: MoodsRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.

         * @param moodsRemoteDataSource the backend data source
         * *
         * @param moodsLocalDataSource  the device storage data source
         * *
         * @return the [MoodsRepository] instance
         */
        @JvmStatic fun getInstance(moodsLocalDataSource: MoodsDataSource): MoodsRepository {
            return INSTANCE
                ?: MoodsRepository(
                    moodsLocalDataSource
                )
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