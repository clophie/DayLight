package com.daylight.addedithabit

import android.content.Context
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitsDataSource
import com.daylight.util.notif.AlarmScheduler
import java.util.*

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
    private val habitsRepository: HabitsDataSource,
    private val addHabitView: AddEditHabitContract.View,
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

    override fun saveHabit(title: String, description: String, days: MutableList<MaterialDayPicker.Weekday>, time: Calendar, context: Context) {
        if (habitId == null) {
            createHabit(title, description, days, time, context)
        } else {
            updateHabit(title, description, days, time, context)
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
            addHabitView.setDays(habit.days)
            addHabitView.setTime(habit.time)
        }
        isDataMissing = false
    }

    override fun onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (addHabitView.isActive) {
            addHabitView.showEmptyHabitError()
        }
    }

    private fun createHabit(title: String, description: String, days: MutableList<MaterialDayPicker.Weekday>,
                            time: Calendar, context: Context) {
        val newHabit = Habit(
            title,
            description,
            days,
            time
        )
        if (newHabit.title.isEmpty()) {
            addHabitView.showEmptyHabitError()
        } else {
            habitsRepository.saveHabit(newHabit)
            addHabitView.showHabitsList()
            AlarmScheduler.scheduleAlarmsForHabit(context, newHabit)
        }
    }

    private fun updateHabit(title: String, description: String, days: MutableList<MaterialDayPicker.Weekday>, time: Calendar, context: Context) {
        if (habitId == null) {
            throw RuntimeException("updateHabit() was called but habit is new.")
        }
        val habit = Habit(
            title,
            description,
            days,
            time,
            habitId
        )
        habitsRepository.saveHabit(habit)
        addHabitView.showHabitsList() // After an edit, go back to the list.
        AlarmScheduler.updateAlarmsForHabit(context, habit)
    }
}
