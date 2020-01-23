package com.example.daylight.habits

import com.example.daylight.data.source.HabitsDataSource

/**
 * Listens to user actions from the UI ([HabitFragment]), retrieves the data and updates
 * the UI as required.
 * @param habitId ID of the task to edit or null for a new task
 *
 * @param habitssRepository a repository of data for tasks
 *
 * @param addHabitView the add/edit view
 *
 * @param isDataMissing whether data needs to be loaded or not (for config changes)
 */

class HabitPresenter(
    private val habitId: Int?,
    val habitsRepository: HabitsDataSource,
    val addHabitView: HabitContract.View,
    override var isDataMissing: Boolean
) : HabitContract.Presenter, HabitsDataSource.GetHabitCallback {

}
