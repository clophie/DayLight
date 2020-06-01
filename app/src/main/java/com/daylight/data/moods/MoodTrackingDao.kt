package com.daylight.data.moods

import androidx.room.*
import com.daylight.data.MoodAndTracking
import com.daylight.data.moods.MoodTracking
import com.daylight.util.Converters
import java.util.*


@Dao
@TypeConverters(Converters::class)
interface MoodTrackingDao {
    /**
     * Select all moods from the moods table.
     *
     * @return all moods.
     */
    @Query("SELECT Moods.score, Moods.moodName, MoodTracking.name, Moods.id, MoodTracking.date FROM Moods LEFT JOIN moodTracking ON MoodTracking.name = Moods.moodName") fun getMoodTracking(): List<MoodAndTracking>

    /**
     * Select a mood by id.
     *
     * @param moodId the mood id.
     */
    @Query("SELECT * FROM MoodTracking WHERE name = :name") fun getMoodTrackingByName(name: String): List<MoodTracking>?

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
    @Query("DELETE FROM MoodTracking WHERE name = :name") fun deleteMoodTrackingByName(name: String): Int

    /**
     * Delete a mood by id.
     *
     * @return the number of moods deleted. This should always be 1.
     */
    @Query("DELETE FROM MoodTracking WHERE date BETWEEN :date AND :date + 86400000") fun deleteMoodTrackingByTimestamp(date: Calendar): Int

    /**
     * Delete all moods.
     */
    @Query("DELETE FROM MoodTracking") fun deleteMoodTracking()
}