package com.example.daylight.addedithabit

import com.example.daylight.data.source.Habit
import com.example.daylight.data.source.HabitsDataSource

/**
 * Listens to user actions from the UI ([AddEditHabitFragment]), retrieves the data and updates
 * the UI as required.
 * @param habitId ID of the habit to edit or null for a new habit
 *
 * @param habitsRepository a repository of data for habits
 *
 * @param addHabitView the add/edit view
 *
 * @param isDataMissing whether data needs to be loaded or not (for config changes)
 */
class AddEditHabitPresenter(
    private val habitId: String?,
    val habitsRepository: HabitsDataSource,
    val addHabitView: AddEditHabitContract.View,
    override var isDataMissing: Boolean
) : AddEditHabitContract.Presenter, HabitsDataSource.GetHabitCallback {

    init {
        addHabitView.presenter = this
    }

    override fun start() {
        if (habitId != null && isDataMissing) {
            populateHabit()
        }
    }

    override fun saveHabit(title: String, description: String) {
        if (habitId == null) {
            createHabit(title, description)
        } else {
            updateHabit(title, description)
        }
    }

    override fun populateHabit() {
        if (habitId == null) {
            throw RuntimeException("populateHabit() was called but habit is new.")
        }
        habitsRepository.getHabit(habitId, this)
    }

    override fun onHabitLoaded(habit: Habit) {
        // The view may not be able to handle UI updates anymore
        if (addHabitView.isActive) {
            addHabitView.setTitle(habit.title)
            addHabitView.setDescription(habit.description)
        }
        isDataMissing = false
    }

    override fun onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (addHabitView.isActive) {
            addHabitView.showEmptyHabitError()
        }
    }

    private fun createHabit(title: String, description: String) {
        val newHabit = Habit(title, description)
        if (newHabit.isEmpty) {
            addHabitView.showEmptyHabitError()
        } else {
            habitsRepository.saveHabit(newHabit)
            addHabitView.showHabitsList()
        }
    }

    private fun updateHabit(title: String, description: String) {
        if (habitId == null) {
            throw RuntimeException("updateHabit() was called but habit is new.")
        }
        habitsRepository.saveHabit(Habit(title, description, habitId))
        addHabitView.showHabitsList() // After an edit, go back to the list.
    }
}
