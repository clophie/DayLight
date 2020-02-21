package com.example.daylight.habitdetail

import com.example.daylight.BasePresenter
import com.example.daylight.BaseView


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

        fun showCompletionStatus(complete: Boolean)

        fun showEditHabit(habitId: String)

        fun showHabitDeleted()

        fun showHabitMarkedComplete()

        fun showHabitMarkedActive()
    }

    interface Presenter : BasePresenter {

        fun editHabit()

        fun deleteHabit()

        fun completeHabit()

        fun activateHabit()
    }
}
