package com.example.daylight.habits

import com.example.daylight.BaseView
import com.example.daylight.BasePresenter
import com.example.daylight.data.source.Habit

/**
 * This specifies the contract between the view and the presenter.
 */
interface  HabitsContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showHabits(habits: List<Habit>)

        fun showAddHabit()

        fun showTrackHabit()

        fun showHabitDetailsUi(habitId: String)

        fun showHabitMarkedComplete()

        fun showHabitMarkedActive()

        fun showCompletedHabitsCleared()

        fun showLoadingHabitsError()

        fun showNoHabits()

        fun showSuccessfullySavedMessage()

        fun showFilteringPopUpMenu()
    }

    interface Presenter : BasePresenter {

        var currentFiltering: HabitsFilterType

        fun result(requestCode: Int, resultCode: Int)

        fun loadHabits(forceUpdate: Boolean)

        fun addNewHabit()

        fun trackHabit()

        fun openHabitDetails(requestedHabit: Habit)
    }
}