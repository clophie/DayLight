package com.example.daylight.data.source.local.habits

import androidx.room.*
import com.example.daylight.data.source.habits.HabitTracking
import com.example.daylight.util.Converters
import java.util.*


@Dao
@TypeConverters(Converters::class)
interface HabitTrackingDao {
    /**
     * Select all habits from the habits table.
     *
     * @return all habits.
     */
    @Query("SELECT * FROM HabitTracking") fun getHabitTracking(): List<HabitTracking>

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
}