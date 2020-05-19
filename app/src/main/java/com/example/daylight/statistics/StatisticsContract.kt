package com.example.daylight.statistics

import com.example.daylight.BasePresenter
import com.example.daylight.BaseView

/**
 * This specifies the contract between the view and the presenter.
 */
interface StatisticsContract {

    interface View : BaseView<Presenter> {
        val isActive: Boolean

        fun setProgressIndicator(active: Boolean)

        fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int)

        fun showLoadingStatisticsError()
    }

    interface Presenter : BasePresenter
}