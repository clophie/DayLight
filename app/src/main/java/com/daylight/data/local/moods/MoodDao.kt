package com.daylight.data.local.moods

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.daylight.data.moods.Mood
import com.daylight.util.Converters

@Dao
@TypeConverters(Converters::class)
interface MoodsDao {
    /**
     * Select all moods from the moods table.
     *
     * @return all moods.
     */
    @Query("SELECT * FROM Moods") fun getMoods(): List<Mood>

    /**
     * Select a mood by id.
     *
     * @param moodId the mood id.
     * @return the mood with moodId.
     */
    @Query("SELECT * FROM Moods WHERE id = :moodId") fun getMoodById(moodId: String): Mood?

    /**
     * Insert a mood in the database. If the mood already exists, replace it.
     *
     * @param mood the mood to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertMood(mood: Mood)

    /**
     * Update a mood.
     *
     * @param mood mood to be updated
     * @return the number of moods updated. This should always be 1.
     */
    @Update
    fun updateMood(mood: Mood): Int

    /**
     * Delete a mood by id.
     *
     * @return the number of moods deleted. This should always be 1.
     */
    @Query("DELETE FROM moods WHERE moodName = :moodName") fun deleteMoodByName(moodName: String): Int

    /**
     * Delete all moods.
     */
    @Query("DELETE FROM Moods") fun deleteMoods()

}