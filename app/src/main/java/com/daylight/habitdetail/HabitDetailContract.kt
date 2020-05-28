package com.daylight.habitdetail

import com.daylight.BasePresenter
import com.daylight.BaseView
import com.daylight.data.habits.HabitTracking
import java.util.*


/**
 * This specifies the contract between the view and the presenter.
 */
interface HabitDetailContract {

    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showMissingHabit()

        fun hideTitle()

        fun showTitle(title: String)

        fun hideDescription()

        fun showDescription(description: String)

        fun showEditHabit(habitId: String)

        fun showHabitDeleted()

        fun showHabitTracking(habitTracking: List<HabitTracking>)

    }

    interface Presenter : BasePresenter {

        fun editHabit()

        fun deleteHabit()

        fun deleteHabitTracking(date: Calendar)

        fun loadHabitTracking()
    }
}
