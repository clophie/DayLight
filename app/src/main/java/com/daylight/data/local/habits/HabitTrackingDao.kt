package com.daylight.data.local.habits

import androidx.room.*
import com.daylight.data.HabitAndTracking
import com.daylight.data.MoodAndHabitTracking
import com.daylight.data.habits.HabitTracking
import com.daylight.util.Converters
import java.util.*


@Dao
@TypeConverters(Converters::class)
interface HabitTrackingDao {
    /**
     * Select all habits from the habits table.
     *
     * @return all habits.
     */
    @Query("SELECT HabitTracking.completionDateTime, Habits.title FROM HabitTracking INNER JOIN Habits on Habits.habitid = HabitTracking.habitid") fun getHabitTracking(): List<HabitAndTracking>

    /**
     * Select a habit by id.
     *
     * @param habitId the habit id.
     */
    @Query("SELECT * FROM HabitTracking WHERE habitid = :habitId") fun getHabitTrackingByHabitId(habitId: String): List<HabitTracking>?

    /**
     * Insert a habit in the database. If the habit already exists, replace it.
     *
     * @param habit the habit to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertHabitTracking(habitTracking: HabitTracking)

    /**
     * Update a habit.
     *
     * @param habit habit to be updated
     * @return the number of habits updated. This should always be 1.
     */
    @Update
    fun updateHabitTracking(habitTracking: HabitTracking): Int

    /**
     * Delete a habit by id.
     *
     * @return the number of habits deleted. This should always be 1.
     */
    @Query("DELETE FROM HabitTracking WHERE habitid = :habitId") fun deleteHabitTrackingByHabitId(habitId: String): Int

    /**
     * Delete a habit by id.
     *
     * @return the number of habits deleted. This should always be 1.
     */
    @Query("DELETE FROM HabitTracking WHERE completionDateTime = :completionDateTime") fun deleteHabitTrackingByTimestamp(completionDateTime: Calendar): Int

    /**
     * Delete all habits.
     */
    @Query("DELETE FROM HabitTracking") fun deleteHabitTracking()

    @Query("SELECT moodName, score, null as name, null as date, null as title, null as habitid, null as completionDateTime, null as habitid FROM Moods UNION\n" +
            "SELECT null as moodName, null as score, name, date, null as title, null as habitid, null as completionDateTime, null as habitid FROM MoodTracking UNION\n" +
            "SELECT null as moodName, null as score, null as name, null as date, title, habitid, null as completionDateTime, null as habitid FROM Habits UNION \n" +
            "SELECT null as moodName, null as score, null as name, null as date, null as title, null as habitid, completionDateTime, habitid FROM habitTracking") fun getDataForCorrelationProcessing() : List<MoodAndHabitTracking>
}