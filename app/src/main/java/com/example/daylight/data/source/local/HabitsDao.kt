package com.example.daylight.data.source.local

import androidx.room.*
import com.example.daylight.data.source.Habit
import com.example.daylight.util.Converters

@Dao
@TypeConverters(Converters::class)
interface HabitsDao {
    /**
     * Select all habits from the habits table.
     *
     * @return all habits.
     */
    @Query("SELECT * FROM Habits") fun getHabits(): List<Habit>

    /**
     * Select a habit by id.
     *
     * @param habitId the habit id.
     * @return the habit with habitId.
     */
    @Query("SELECT * FROM Habits WHERE habitid = :habitId") fun getHabitById(habitId: String): Habit?

    /**
     * Insert a habit in the database. If the habit already exists, replace it.
     *
     * @param habit the habit to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertHabit(habit: Habit)

    /**
     * Update a habit.
     *
     * @param habit habit to be updated
     * @return the number of habits updated. This should always be 1.
     */
    @Update
    fun updateHabit(habit: Habit): Int

    /**
     * Update the complete status of a habit
     *
     * @param habitId    id of the habit
     * @param completed status to be updated
     */
    @Query("UPDATE habits SET completed = :completed WHERE habitid = :habitId")
    fun updateCompleted(habitId: String, completed: Boolean)

    /**
     * Delete a habit by id.
     *
     * @return the number of habits deleted. This should always be 1.
     */
    @Query("DELETE FROM Habits WHERE habitid = :habitId") fun deleteHabitById(habitId: String): Int

    /**
     * Delete all habits.
     */
    @Query("DELETE FROM Habits") fun deleteHabits()
}