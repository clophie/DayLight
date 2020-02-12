package com.example.daylight.addedithabit

import com.example.daylight.BasePresenter
import com.example.daylight.BaseView

interface AddEditHabitContract {

    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun showEmptyHabitError()

        fun showHabitsList()

        fun setTitle(title: String)

        fun setDescription(description: String)

    }

    interface Presenter : BasePresenter {
        var isDataMissing: Boolean

        fun saveHabit(title: String, description: String)

        fun populateHabit()
    }
}
