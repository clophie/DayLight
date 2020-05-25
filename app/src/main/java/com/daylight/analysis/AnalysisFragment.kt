package com.daylight.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.daylight.R
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.util.AppExecutors


/**
 * Main UI for the analysis screen.
 */
class AnalysisFragment : Fragment(), AnalysisContract.View {

    private lateinit var analysisTV: TextView

    override lateinit var presenter: AnalysisContract.Presenter

    override val isActive: Boolean
        get() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.analysis_frag, container, false)
        analysisTV = root.findViewById(R.id.analysis)
        return root
    }

    override fun onResume() {
        super.onResume()
        if (!this::presenter.isInitialized) {
            val database = DaylightDatabase.getInstance(getActivity()!!.getApplicationContext())
            val repo = HabitsRepository.getInstance(
                HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

            AnalysisPresenter(repo, this)
        }

        presenter.start()
    }

    override fun setProgressIndicator(active: Boolean) {
        if (active) {
            analysisTV.text = getString(R.string.loading)
        } else {
            analysisTV.text = ""
        }
    }

    override fun showAnalysis(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0) {
            analysisTV.text = resources.getString(R.string.analysis_no_habits)
        } else {
            val displayString = "${resources.getString(R.string.analysis_active_habits)} " +
                    "$numberOfIncompleteTasks\n" +
                    "${resources.getString(R.string.analysis_completed_habits)} " +
                    "$numberOfCompletedTasks"
            analysisTV.text = displayString
        }
    }

    override fun showLoadingAnalysisError() {
        analysisTV.text = resources.getString(R.string.analysis_error)
    }

    companion object {

        fun newInstance(): AnalysisFragment {
            return AnalysisFragment()
        }
    }
}
