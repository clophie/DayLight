package com.daylight.analysis

import com.anychart.chart.common.dataentry.HeatDataEntry
import com.daylight.R
import com.daylight.data.MoodAndTracking
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodTracking
import com.daylight.data.moods.MoodsDataSource
import com.daylight.data.moods.MoodsRepository
import java.util.*
import kotlin.collections.ArrayList


/**
 * Listens to user actions from the UI ([AnalysisFragment]), retrieves the data and updates
 * the UI as required.
 */
class AnalysisPresenter(
    val habitsRepository: HabitsRepository,
    val moodsRepository: MoodsRepository,
    val analysisView: AnalysisContract.View

) : AnalysisContract.Presenter {

    init {
        analysisView.presenter = this
    }

    override fun getDataForMoodChart(){
        moodsRepository.getMoodTracking( object : MoodsDataSource.GetMoodTrackingAndMoodCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodAndTracking>) {
                generateMoodChartData(moodTracking)
            }

            override fun onDataNotAvailable() { }

        })
    }

    override fun start() {
        loadAnalysis()
    }

    private fun loadAnalysis() {
/*        habitsRepository.getHabitTracking(object : HabitsDataSource.GetHabitTrackingCallback {
            override fun onHabitTrackingLoaded(habitTracking: List<HabitTracking>) {
                habitTrackingList = habitTracking
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!analysisView.isActive) {
                    return
                }
                analysisView.showLoadingAnalysisError()
            }
        })*/


    }

    private fun generateMoodChartData(moodTracking: List<MoodAndTracking>)  {
        val data = arrayListOf<CustomHeatDataEntry>()
        var color = ""
        moodTracking.forEach {
            when (it.score) {
                1 -> color = analysisView.getMoodScoreColor1()
                2 -> color = analysisView.getMoodScoreColor2()
                3 -> color = analysisView.getMoodScoreColor3()
                4 -> color = analysisView.getMoodScoreColor4()
                5 -> color = analysisView.getMoodScoreColor5()
            }

            data.add(CustomHeatDataEntry(it.date.get(Calendar.DAY_OF_WEEK).toString(), it.date.get(Calendar.WEEK_OF_MONTH).toString(), it.date.get(Calendar.DAY_OF_MONTH), color))
        }

        analysisView.generateMoodChart(data)
    }

    class CustomHeatDataEntry internal constructor(
        x: String?,
        y: String?,
        day: Int?,
        fill: String?
    ) :
        HeatDataEntry(x, y, day) {
        init {
            setValue("fill", fill)
        }
    }
}
