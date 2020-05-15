package com.example.daylight.trackhabit

import com.example.daylight.BasePresenter
import com.example.daylight.BaseView
import com.example.daylight.data.source.Habit

interface TrackHabitContract {

    interface View : BaseView<Presenter> {
        fun populateSpinner(habits: List<Habit>)
    }

    interface Presenter : BasePresenter {

        fun submitTracking()

        fun loadHabits()
    }
}