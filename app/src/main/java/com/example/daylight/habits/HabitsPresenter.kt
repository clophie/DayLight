package com.example.daylight.habits

import android.app.Activity
import com.example.daylight.addedithabit.AddEditHabitActivity
import com.example.daylight.data.source.Habit
import com.example.daylight.data.source.HabitsDataSource
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
        loadHabits(forceUpdate || firstLoad, true)
        firstLoad = false
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
                    when (currentFiltering) {
                        HabitsFilterType.ALL_HABITS -> habitsToShow.add(habit)
                        HabitsFilterType.ACTIVE_HABITS -> if (habit.isActive) {
                            habitsToShow.add(habit)
                        }
                        HabitsFilterType.COMPLETED_HABITS -> if (habit.isCompleted) {
                            habitsToShow.add(habit)
                        }
                    }
                }
                // The view may not be able to handle UI updates anymore
                if (!habitsView.isActive) {
                    return
                }
                if (showLoadingUI) {
                    habitsView.setLoadingIndicator(false)
                }

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
            processEmptyHabits()
        } else {
            // Show the list of habits
            habitsView.showHabits(habits)
            // Set the filter label's text.
            showFilterLabel()
        }
    }

    private fun showFilterLabel() {
        when (currentFiltering) {
            HabitsFilterType.ACTIVE_HABITS -> habitsView.showActiveFilterLabel()
            HabitsFilterType.COMPLETED_HABITS -> habitsView.showCompletedFilterLabel()
            else -> habitsView.showAllFilterLabel()
        }
    }

    private fun processEmptyHabits() {
        when (currentFiltering) {
            HabitsFilterType.ACTIVE_HABITS -> habitsView.showNoActiveHabits()
            HabitsFilterType.COMPLETED_HABITS -> habitsView.showNoCompletedHabits()
            else -> habitsView.showNoHabits()
        }
    }

    override fun addNewHabit() {
        habitsView.showAddHabit()
    }

    override fun trackHabit() {
        habitsView.showTrackHabit()
    }

    override fun openHabitDetails(requestedHabit: Habit) {
        habitsView.showHabitDetailsUi(requestedHabit.id)
    }

    override fun completeHabit(completedHabit: Habit) {
        habitsRepository.completeHabit(completedHabit)
        habitsView.showHabitMarkedComplete()
        loadHabits(false, false)
    }

    override fun activateHabit(activeHabit: Habit) {
        habitsRepository.activateHabit(activeHabit)
        habitsView.showHabitMarkedActive()
        loadHabits(false, false)
    }

}
