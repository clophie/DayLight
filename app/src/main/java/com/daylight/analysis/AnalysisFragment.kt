package com.daylight.analysis

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.daylight.R
import com.daylight.data.HabitAndTracking
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.util.AppExecutors
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.trackhabit_frag.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Main UI for the analysis screen.
 */
class AnalysisFragment : Fragment(), AnalysisContract.View {

    private lateinit var moodChart : LineChart
    private lateinit var habitChart : PieChart
    private lateinit var correlationText : TextView

    override lateinit var presenter: AnalysisContract.Presenter

    override val isActive: Boolean
        get() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.analysis_frag, container, false)

        with(root) {
            moodChart = findViewById(R.id.moodsChart)
            habitChart = findViewById(R.id.habitChart)
            correlationText = findViewById(R.id.correlationText)
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
        presenter.getDataForHabitChart()
        presenter.getCorrelations()

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

    override fun showLoadingAnalysisError() {
    }

    override fun generateMoodChart(data: ArrayList<Entry>, latestDate: Calendar) {
        val dataSet = LineDataSet(data, "")
        dataSet.label = ""
        dataSet.lineWidth = 3F

        val lineData = LineData(dataSet)
        lineData.setDrawValues(false)

        moodChart.data = lineData
        moodChart.xAxis.valueFormatter = MoodXAxisFormatter()
        moodChart.axisLeft.axisMinimum = 1F
        moodChart.axisLeft.axisMaximum = 5F
        moodChart.axisLeft.granularity = 1F
        moodChart.axisRight.axisMinimum = 0F
        moodChart.axisRight.axisMaximum = 5F
        moodChart.axisRight.granularity = 1F
        moodChart.xAxis.granularity = 1F
        moodChart.xAxis.axisMaximum = latestDate.get(Calendar.DAY_OF_YEAR).toFloat()
        latestDate.add(Calendar.HOUR, - 30*24)
        moodChart.xAxis.axisMinimum = latestDate.get(Calendar.DAY_OF_YEAR).toFloat()
        moodChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        moodChart.animateY(1400, Easing.EaseInOutQuad)
        moodChart.legend.isEnabled = false
        moodChart.description.isEnabled = false
        moodChart.setPinchZoom(false)
        moodChart.invalidate()
    }

    override fun generateHabitChart(data: ArrayList<PieEntry>, habitAndTracking: List<HabitAndTracking>) {
        val dataSet = PieDataSet(data, "")
        dataSet.colors = getColors()

        val pieData = PieData(dataSet)
        pieData.setDrawValues(false)

        habitChart.data = pieData
        habitChart.description.isEnabled = false
        habitChart.legend.isEnabled = false
        habitChart.animateY(1400, Easing.EaseInOutQuad)
        habitChart.invalidate()
    }

    @SuppressLint("SetTextI18n")
    override fun loadCorrelation(habitName: String) {
        if (habitName.isNotEmpty()) {
            correlationText.text = "When you complete your habit - ${habitName}, your mood tends to be more positive!"
        }
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

    class MoodXAxisFormatter : ValueFormatter() {
        override fun getAxisLabel(dayOfYear: Float, axis: AxisBase?): String? {
            val c = Calendar.getInstance()
            c.set(Calendar.DAY_OF_YEAR, dayOfYear.toInt())

            return "${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH)}"
        }
    }

    fun getColors() : ArrayList<Int> {
        val colors : ArrayList<Int> = mutableListOf<Int>() as ArrayList<Int>

        ColorTemplate.COLORFUL_COLORS.forEach {
            colors.add(it)
        }

        ColorTemplate.JOYFUL_COLORS.forEach {
            colors.add(it)
        }

        ColorTemplate.PASTEL_COLORS.forEach {
            colors.add(it)
        }

        return colors
    }
}
