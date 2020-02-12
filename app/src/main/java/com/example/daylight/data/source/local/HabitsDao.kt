package com.example.daylight.data.source.local

import androidx.room.*
import com.example.daylight.data.source.Habit

@Dao
interface HabitsDao {
    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM Habits") fun getHabits(): List<Habit>

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM Habits WHERE entryid = :habitd") fun getHabitById(habitId: String): Habit?

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertHabit(habit: Habit)

    /**
     * Update a task.
     *
     * @param task task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    fun updateHabit(habit: Habit): Int

    /**
     * Update the complete status of a task
     *
     * @param taskId    id of the task
     * @param completed status to be updated
     */
    @Query("UPDATE habits SET completed = :completed WHERE entryid = :habitId")
    fun updateCompleted(habitId: String, completed: Boolean)

    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM Habits WHERE entryid = :habitId") fun deleteHabitById(habitId: String): Int

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM Habits") fun deleteHabits()
}