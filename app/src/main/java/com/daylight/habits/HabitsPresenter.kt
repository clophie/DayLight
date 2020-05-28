package com.daylight.habits

import android.app.Activity
import com.daylight.addedithabit.AddEditHabitActivity
import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitsDataSource
import com.daylight.moods.MoodsContract
import com.daylight.moods.MoodsPresenter
import java.util.ArrayList

/**
 * Listens to user actions from the UI ([HabitFragment]), retrieves the data and updates
 * the UI as required.
 * @param habitId ID of the habit to edit or null for a new habit
 *
 * @param habitssRepository a repository of data for habits
 *
 * @param addHabitView the add/edit view
 *
 * @param isDataMissing whether data needs to be loaded or not (for config changes)
 */

class HabitsPresenter(
    val habitsRepository: HabitsDataSource,
    val habitsView: HabitsContract.View
) : HabitsContract.Presenter {

    override var currentFiltering = HabitsFilterType.ALL_HABITS

    private var firstLoad = true

    init {
        habitsView.presenter = this
    }

    override fun start() {
        loadHabits(false)
    }

    override fun result(requestCode: Int, resultCode: Int) {
        // If a habit was successfully added, show snackbar
        if (AddEditHabitActivity.REQUEST_ADD_HABIT ==
            requestCode && Activity.RESULT_OK == resultCode
        ) {
            habitsView.showSuccessfullySavedMessage()
        }
    }

    override fun loadHabits(forceUpdate: Boolean) {
        loadHabits(forceUpdate || firstLoad, false)
        firstLoad = false
    }

    override fun confirmDelete(requestedHabit: Habit) {
        habitsView.showConfirmDelete(requestedHabit)
    }

    override fun deleteHabit(requestedHabit: Habit) {
        habitsRepository.deleteHabit(requestedHabit.id)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [HabitsDataSource]
     * *
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadHabits(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            habitsView.setLoadingIndicator(true)
        }
        if (forceUpdate) {
            habitsRepository.refreshHabits()
        }

        habitsRepository.getHabits(object : HabitsDataSource.LoadHabitsCallback {
            override fun onHabitsLoaded(habits: List<Habit>) {
                val habitsToShow = ArrayList<Habit>()

                // We filter the habits based on the requestType
                for (habit in habits) {
                    habitsToShow.add(habit)
                }
                // The view may not be able to handle UI updates anymore
                if (!habitsView.isActive) {
                    return
                }

                habitsView.setLoadingIndicator(false)

                processHabits(habitsToShow)
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!habitsView.isActive) {
                    return
                }
                habitsView.showLoadingHabitsError()
            }
        })
    }

    private fun processHabits(habits: List<Habit>) {
        if (habits.isEmpty()) {
            // Show a message indicating there are no habits for that filter type.
            habitsView.showNoHabits()
        } else {
            // Show the list of habits
            habitsView.showHabits(habits)
        }
    }

    override fun addNewHabit() {
        habitsView.showAddHabit()
    }

    override fun addNewMood() {
        habitsView.showAddMood()
    }

    override fun trackHabit() {
        habitsView.showTrackHabit()
    }

    override fun trackMood() {
        habitsView.showTrackMood()
    }

    override fun openHabitDetails(requestedHabit: Habit) {
        habitsView.showHabitDetailsUi(requestedHabit.id)
    }
}
