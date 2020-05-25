package com.daylight.analysis

import com.daylight.BasePresenter
import com.daylight.BaseView

/**
 * This specifies the contract between the view and the presenter.
 */
interface AnalysisContract {

    interface View : BaseView<Presenter> {
        val isActive: Boolean

        fun setProgressIndicator(active: Boolean)

        fun showAnalysis(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int)

        fun showLoadingAnalysisError()
    }

    interface Presenter : BasePresenter
}