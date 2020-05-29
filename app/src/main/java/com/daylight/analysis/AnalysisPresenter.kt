package com.daylight.analysis

import com.daylight.R
import com.daylight.data.HabitAndTracking
import com.daylight.data.MoodAndHabitTracking
import com.daylight.data.MoodAndTracking
import com.daylight.data.habits.HabitTracking
import com.daylight.data.habits.HabitsDataSource
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodTracking
import com.daylight.data.moods.MoodsDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.util.CorrelationProcessing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
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

    override fun getDataForHabitChart() {
        habitsRepository.getHabitTracking( object : HabitsDataSource.GetHabitAndTrackingCallback {
            override fun onHabitTrackingLoaded(habitTracking: List<HabitAndTracking>) {
                generateHabitChartData(habitTracking)
            }

            override fun onDataNotAvailable() { }
        })
    }

    override fun start() {
        loadAnalysis()
    }

    private fun loadAnalysis() { }

    private fun generateMoodChartData(moodTracking: List<MoodAndTracking>)  {
        val data = arrayListOf<Entry>()
        var latestDate = Calendar.getInstance()
        latestDate.set(Calendar.YEAR, 0)

        moodTracking.forEach {
            if (!it.name.isNullOrEmpty()) {
                data.add(Entry(it.date?.get(Calendar.DAY_OF_YEAR)!!.toFloat(), it.score.toFloat()))

                if (it.date!! > latestDate) {
                    latestDate = it.date
                }
            }
        }

        data.sortBy { it.x }

        analysisView.generateMoodChart(data, latestDate)
    }

    private fun generateHabitChartData(habitTracking: List<HabitAndTracking>) {
        val data = arrayListOf<PieEntry>()

        val trackedTitles = mutableListOf<String>()

        habitTracking.forEach { h ->
            if (!trackedTitles.contains(h.title)) {
                trackedTitles.add(h.title)

                val count = habitTracking.filter { it.title == h.title}.count().toFloat()

                data.add(
                    PieEntry(count, h.title)
                )
            }
        }

        analysisView.generateHabitChart(data, habitTracking)
    }

    override fun getCorrelations() {
        habitsRepository.getDataForCorrelationProcessing( object : HabitsDataSource.GetCorrelationDataCallback {
            override fun onCorrelationDataLoaded(moodAndHabitTracking: List<MoodAndHabitTracking>) {
                val habitFound = CorrelationProcessing.findCorrelations(moodAndHabitTracking)

                analysisView.loadCorrelation(habitFound)
            }

            override fun onDataNotAvailable() { }
        })
    }

}
