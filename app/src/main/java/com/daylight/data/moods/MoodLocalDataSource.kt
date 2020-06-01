package com.daylight.data.moods

import com.daylight.util.AppExecutors
import java.util.*

/**
 * Concrete implementation of a data source as a db.
 */
class MoodsLocalDataSource private constructor(
    private val appExecutors: AppExecutors,
    private val moodsDao: MoodsDao,
    val moodTrackingDao: MoodTrackingDao
) : MoodsDataSource {

    /**
     * Note: [MoodsDataSource.LoadMoodsCallback.onDataNotAvailable] is fired if the database doesn't exist
     * or the table is empty.
     */
    override fun getMoods(callback: MoodsDataSource.LoadMoodsCallback) {
        appExecutors.diskIO.execute {
            val moods = moodsDao.getMoods()
            appExecutors.mainThread.execute {
                if (moods.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable()
                } else {
                    callback.onMoodsLoaded(moods)
                }
            }
        }
    }

    /**
     * Note: [MoodsDataSource.GetMoodCallback.onDataNotAvailable] is fired if the [Mood] isn't
     * found.
     */
    override fun getMood(moodId: String, callback: MoodsDataSource.GetMoodCallback) {
        appExecutors.diskIO.execute {
            val mood = moodsDao.getMoodById(moodId)
            appExecutors.mainThread.execute {
                if (mood != null) {
                    callback.onMoodLoaded(mood)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun saveMood(mood: Mood) {
        appExecutors.diskIO.execute { moodsDao.insertMood(mood) }
    }

    override fun refreshMoods() {
        // Not required because the {@link MoodsRepository} handles the logic of refreshing the
        // moods from all the available data sources.
    }

    override fun deleteAllMoods() {
        appExecutors.diskIO.execute { moodsDao.deleteMoods() }
    }

    override fun deleteMood(moodName: String) {
        appExecutors.diskIO.execute { moodsDao.deleteMoodByName(moodName) }
    }

    override fun getMoodTracking(callback: MoodsDataSource.GetMoodTrackingAndMoodCallback) {
        appExecutors.diskIO.execute {
            val moodTracking = moodTrackingDao.getMoodTracking()
            appExecutors.mainThread.execute {
                callback.onMoodTrackingLoaded(moodTracking)
            }
        }
    }

    override fun getMoodTrackingByName(name: String, callback: MoodsDataSource.GetMoodTrackingCallback) {
        appExecutors.diskIO.execute {
            val moodTracking = moodTrackingDao.getMoodTrackingByName(name)
            appExecutors.mainThread.execute {
                if (moodTracking != null) {
                    callback.onMoodTrackingLoaded(moodTracking)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun insertMoodTracking(moodTracking: MoodTracking) {
        appExecutors.diskIO.execute { moodTrackingDao.insertMoodTracking(moodTracking) }
    }

    override fun updateMoodTracking(moodTracking: MoodTracking) {
        appExecutors.diskIO.execute { moodTrackingDao.updateMoodTracking(moodTracking) }
    }

    override fun deleteMoodTrackingByName(name: String) {
        appExecutors.diskIO.execute { moodTrackingDao.deleteMoodTrackingByName(name) }
    }

    override fun deleteMoodTrackingByTimestamp(timestamp: Calendar) {
        appExecutors.diskIO.execute { moodTrackingDao.deleteMoodTrackingByTimestamp(timestamp) }
    }

    override fun deleteAllMoodTracking() {
        appExecutors.diskIO.execute { moodTrackingDao.deleteMoodTracking() }
    }

    companion object {
        private var INSTANCE: MoodsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, moodsDao: MoodsDao, moodTrackingDao: MoodTrackingDao): MoodsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(MoodsLocalDataSource::javaClass) {
                    INSTANCE =
                        MoodsLocalDataSource(
                            appExecutors,
                            moodsDao,
                            moodTrackingDao
                        )
                }
            }
            return INSTANCE!!
        }

    }
}