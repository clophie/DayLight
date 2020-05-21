package com.daylight.data.moods

import java.util.*


/**
 * Main entry point for accessing tasks data.
 *
 *
 * For simplicity, only getMoods() and getMood() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */

interface MoodsDataSource {

    interface LoadMoodsCallback {

        fun onMoodsLoaded(moods: List<Mood>)

        fun onDataNotAvailable()
    }

    interface GetMoodCallback {

        fun onMoodLoaded(mood: Mood)

        fun onDataNotAvailable()
    }

    interface GetMoodTrackingCallback {

        fun onMoodTrackingLoaded(moodTracking: List<MoodTracking>)

        fun onDataNotAvailable()
    }

    fun getMoods(callback: LoadMoodsCallback)

    fun getMood(moodId: String, callback: GetMoodCallback)

    fun saveMood(mood: Mood)

    fun refreshMoods()

    fun deleteAllMoods()

    fun deleteMood(moodId: String)

    fun getMoodTracking()

    fun getMoodTrackingByMoodId(moodId: String, callback: GetMoodTrackingCallback)

    fun insertMoodTracking(moodTracking: MoodTracking)

    fun updateMoodTracking(moodTracking: MoodTracking)

    fun deleteMoodTrackingByMoodId(moodId: String)

    fun deleteMoodTrackingByTimestamp(timestamp: Calendar)

    fun deleteAllMoodTracking()
}