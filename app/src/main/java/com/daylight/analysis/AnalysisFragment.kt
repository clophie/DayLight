package com.daylight.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.daylight.R
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.util.AppExecutors


/**
 * Main UI for the analysis screen.
 */
class AnalysisFragment : Fragment(), AnalysisContract.View {

    private lateinit var moodChart : AnyChartView

    override lateinit var presenter: AnalysisContract.Presenter

    override val isActive: Boolean
        get() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.analysis_frag, container, false)

        with(root) {
            moodChart = findViewById(R.id.moodsChart)
        }

        if (!this::presenter.isInitialized) {
            val database = DaylightDatabase.getInstance(getActivity()!!.getApplicationContext())
            val repo = HabitsRepository.getInstance(
                HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

            val moodsRepo = MoodsRepository.getInstance(
                MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

            AnalysisPresenter(repo, moodsRepo, this)
        }

        presenter.start()
        presenter.getDataForMoodChart()

        return root
    }

    override fun onResume() {
        super.onResume()
        if (!this::presenter.isInitialized) {
            val database = DaylightDatabase.getInstance(getActivity()!!.getApplicationContext())
            val repo = HabitsRepository.getInstance(
                HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

            val moodsRepo = MoodsRepository.getInstance(
                MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

            AnalysisPresenter(repo, moodsRepo, this)
        }

        presenter.start()
    }

    override fun showAnalysis(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {

    }

    override fun showLoadingAnalysisError() {
    }

    override fun generateMoodChart(data: ArrayList<AnalysisPresenter.CustomHeatDataEntry>) {
        val chart = AnyChart.heatMap()
        chart.labels().enabled(false)
        chart.title().enabled(false)

        chart.data(data as List<DataEntry>?)
        moodChart.setChart(chart)
    }

    override fun getMoodScoreColor1(): String {
        return resources.getString(R.string.moodScore1)
    }

    override fun getMoodScoreColor2(): String {
        return resources.getString(R.string.moodScore2)
    }

    override fun getMoodScoreColor3(): String {
        return resources.getString(R.string.moodScore3)
    }

    override fun getMoodScoreColor4(): String {
        return resources.getString(R.string.moodScore4)
    }

    override fun getMoodScoreColor5(): String {
        return resources.getString(R.string.moodScore5)
    }

    companion object {

        fun newInstance(): AnalysisFragment {
            return AnalysisFragment()
        }
    }
}
