package com.daylight.analysis

import com.anychart.chart.common.dataentry.HeatDataEntry
import com.daylight.BasePresenter
import com.daylight.BaseView

/**
 * This specifies the contract between the view and the presenter.
 */
interface AnalysisContract {

    interface View : BaseView<Presenter> {
        val isActive: Boolean

        fun showAnalysis(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int)

        fun showLoadingAnalysisError()

        fun generateMoodChart(data: ArrayList<AnalysisPresenter.CustomHeatDataEntry>)

        fun getMoodScoreColor1() : String

        fun getMoodScoreColor2() : String

        fun getMoodScoreColor3() : String

        fun getMoodScoreColor4() : String

        fun getMoodScoreColor5() : String
    }

    interface Presenter : BasePresenter {
        fun getDataForMoodChart()
    }
}