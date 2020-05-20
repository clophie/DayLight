package com.example.daylight.data.source.local.moods

import androidx.room.*
import com.example.daylight.data.source.moods.MoodTracking
import com.example.daylight.util.Converters
import java.util.*


@Dao
@TypeConverters(Converters::class)
interface MoodTrackingDao {
    /**
     * Select all moods from the moods table.
     *
     * @return all moods.
     */
    @Query("SELECT * FROM MoodTracking") fun getMoodTracking(): List<MoodTracking>

    /**
     * Select a mood by id.
     *
     * @param moodId the mood id.
     */
    @Query("SELECT * FROM MoodTracking WHERE moodid = :moodId") fun getMoodTrackingByMoodId(moodId: String): List<MoodTracking>?

    /**
     * Insert a mood in the database. If the mood already exists, replace it.
     *
     * @param mood the mood to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertMoodTracking(moodTracking: MoodTracking)

    /**
     * Update a mood.
     *
     * @param mood mood to be updated
     * @return the number of moods updated. This should always be 1.
     */
    @Update
    fun updateMoodTracking(moodTracking: MoodTracking): Int

    /**
     * Delete a mood by id.
     *
     * @return the number of moods deleted. This should always be 1.
     */
    @Query("DELETE FROM MoodTracking WHERE moodid = :moodId") fun deleteMoodTrackingByMoodId(moodId: String): Int

    /**
     * Delete a mood by id.
     *
     * @return the number of moods deleted. This should always be 1.
     */
    @Query("DELETE FROM MoodTracking WHERE date = :date") fun deleteMoodTrackingByTimestamp(date: Calendar): Int

    /**
     * Delete all moods.
     */
    @Query("DELETE FROM MoodTracking") fun deleteMoodTracking()
}