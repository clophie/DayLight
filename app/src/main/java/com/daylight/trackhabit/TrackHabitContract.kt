package com.daylight.trackhabit

import com.daylight.BasePresenter
import com.daylight.BaseView
import com.daylight.data.habits.Habit
import java.util.*

interface TrackHabitContract {

    interface View : BaseView<Presenter> {
        fun populateSpinner(habits: List<Habit>)
    }

    interface Presenter : BasePresenter {

        fun submitTracking(habitid: String, completionDateTime: Calendar)

        fun loadHabits()
    }
}