package com.daylight.habits

import com.daylight.BaseView
import com.daylight.BasePresenter
import com.daylight.data.habits.Habit

/**
 * This specifies the contract between the view and the presenter.
 */
interface  HabitsContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showHabits(habits: List<Habit>)

        fun showAddHabit()

        fun showAddMood()

        fun showTrackHabit()

        fun showTrackMood()

        fun showHabitDetailsUi(habitId: String)

        fun showLoadingHabitsError()

        fun showNoHabits()

        fun showSuccessfullySavedMessage()
    }

    interface Presenter : BasePresenter {

        var currentFiltering: HabitsFilterType

        fun result(requestCode: Int, resultCode: Int)

        fun loadHabits(forceUpdate: Boolean)

        fun addNewHabit()

        fun addNewMood()

        fun trackHabit()

        fun trackMood()

        fun openHabitDetails(requestedHabit: Habit)
    }
}