package com.daylight.analysis

import com.daylight.BasePresenter
import com.daylight.BaseView
import com.github.mikephil.charting.data.Entry
import java.util.*
import kotlin.collections.ArrayList

/**
 * This specifies the contract between the view and the presenter.
 */
interface AnalysisContract {

    interface View : BaseView<Presenter> {
        val isActive: Boolean

        fun showAnalysis(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int)

        fun showLoadingAnalysisError()

        fun generateMoodChart(data: ArrayList<Entry>, latestDate: Calendar)

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