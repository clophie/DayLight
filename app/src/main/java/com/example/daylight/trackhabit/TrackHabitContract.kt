package com.example.daylight.trackhabit

import com.example.daylight.BasePresenter
import com.example.daylight.BaseView

interface TrackHabitContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {

        fun submitTracking()
    }
}