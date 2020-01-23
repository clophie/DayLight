package com.example.daylight.habits

import com.example.daylight.BaseView
import com.example.daylight.BasePresenter

/**
 * This specifies the contract between the view and the presenter.
 */
interface  HabitContract {

    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun showEmptyHabitError()

        fun showHabitsList()

        fun setTitle(title: String)

        fun setDescription(description: String)
    }

    interface Presenter : BasePresenter {
        var isDataMissing: Boolean

        fun saveTask(title: String, description: String)

        fun populateTask()
    }
}