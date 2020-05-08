package com.example.daylight.addedithabit

import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.daylight.BasePresenter
import com.example.daylight.BaseView
import java.text.SimpleDateFormat
import java.util.*

interface AddEditHabitContract {

    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun showEmptyHabitError()

        fun showHabitsList()

        fun setTitle(title: String)

        fun setDescription(description: String)

        fun setDays(days: List<MaterialDayPicker.Weekday>)

        fun setTime(time: Calendar)
    }

    interface Presenter : BasePresenter {
        var isDataMissing: Boolean

        fun saveHabit(title: String, description: String, days: MutableList<MaterialDayPicker.Weekday>, time: Calendar)

        fun populateHabit()
    }
}
