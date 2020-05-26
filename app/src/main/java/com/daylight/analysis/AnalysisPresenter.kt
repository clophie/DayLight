package com.daylight.analysis

import com.anychart.chart.common.dataentry.HeatDataEntry
import com.daylight.R
import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitTracking
import com.daylight.data.habits.HabitsDataSource
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

    lateinit var habitsList: List<Habit>
    lateinit var moodsList: List<Mood>
    lateinit var habitTrackingList: List<HabitTracking>
    lateinit var moodTrackingList: List<MoodTracking>

    init {
        analysisView.presenter = this
    }

    override fun getDataForMoodChart(): ArrayList<CustomHeatDataEntry> {
        var data = arrayListOf<CustomHeatDataEntry>()

        moodTrackingList.forEach { moodTracking ->
            var color = ""
            val moodScore = moodsList.filter {mood -> mood.name == moodTracking.moodName}
            when (moodScore[0].score) {
                1 -> color = R.color.moodScore1.toString()
                2 -> color = R.color.moodScore2.toString()
                3 -> color = R.color.moodScore3.toString()
                4 -> color = R.color.moodScore4.toString()
                5 -> color = R.color.moodScore5.toString()
            }

            data.add(CustomHeatDataEntry(moodTracking.date.get(Calendar.DAY_OF_WEEK).toString(), moodTracking.date.get(Calendar.WEEK_OF_MONTH).toString(), moodTracking.date.get(Calendar.DAY_OF_MONTH), color))
        }

        return data
    }

    override fun start() {
        loadAnalysis()
    }

    private fun loadAnalysis() {
        habitsRepository.getHabitTracking(object : HabitsDataSource.GetHabitTrackingCallback {
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
        })

        moodsRepository.getMoodTracking(object : MoodsDataSource.GetMoodTrackingCallback {
            override fun onMoodTrackingLoaded(moodTracking: List<MoodTracking>) {
                moodTrackingList = moodTracking
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!analysisView.isActive) {
                    return
                }
                analysisView.showLoadingAnalysisError()
            }
        })

    }

    class CustomHeatDataEntry internal constructor(
        x: String?,
        y: String?,
        heat: Int?,
        fill: String?
    ) :
        HeatDataEntry(x, y, heat) {
        init {
            setValue("fill", fill)
        }
    }
}
