package com.example.daylight.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.daylight.R


/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment(), StatisticsContract.View {

    private lateinit var statisticsTV: TextView

    override lateinit var presenter: StatisticsContract.Presenter

    override val isActive: Boolean
        get() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.statistics_act, container, false)
        statisticsTV = root.findViewById(R.id.statistics)
        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun setProgressIndicator(active: Boolean) {
        if (active) {
            statisticsTV.text = getString(R.string.loading)
        } else {
            statisticsTV.text = ""
        }
    }

    override fun showStatistics(numberOfIncompleteHabits: Int, numberOfCompletedHabits: Int) {
        if (numberOfCompletedHabits == 0 && numberOfIncompleteHabits == 0) {
            statisticsTV.text = resources.getString(R.string.statistics_no_habits)
        } else {
            val displayString = "${resources.getString(R.string.statistics_active_habits)} " +
                    "$numberOfIncompleteHabits\n" +
                    "${resources.getString(R.string.statistics_completed_habits)} " +
                    "$numberOfCompletedHabits"
            statisticsTV.text = displayString
        }
    }

    override fun showLoadingStatisticsError() {
        statisticsTV.text = resources.getString(R.string.statistics_error)
    }

    companion object {

        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
