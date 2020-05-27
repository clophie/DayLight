package com.daylight.analysis

import com.daylight.R
import com.daylight.data.MoodAndTracking
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodTracking
import com.daylight.data.moods.MoodsDataSource
import com.daylight.data.moods.MoodsRepository
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
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
        val data = arrayListOf<Entry>()
        moodTracking.forEach {
            data.add(Entry(it.date.get(Calendar.DAY_OF_YEAR).toFloat(), it.score.toFloat()))
        }

        analysisView.generateMoodChart(data)
    }

}
