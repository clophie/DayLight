package com.daylight.analysis

import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitsDataSource
import com.daylight.data.habits.HabitsRepository


/**
 * Listens to user actions from the UI ([AnalysisFragment]), retrieves the data and updates
 * the UI as required.
 */
class AnalysisPresenter(
    val habitsRepository: HabitsRepository,
    val analysisView: AnalysisContract.View
) : AnalysisContract.Presenter {

    init {
        analysisView.presenter = this
    }

    override fun start() {
        loadAnalysis()
    }

    private fun loadAnalysis() {
        analysisView.setProgressIndicator(true)

        habitsRepository.getHabits(object : HabitsDataSource.LoadHabitsCallback {
            override fun onHabitsLoaded(habits: List<Habit>) {
                // We calculate number of active and completed habits

                // The view may not be able to handle UI updates anymore
                if (!analysisView.isActive) {
                    return
                }
                analysisView.setProgressIndicator(false)
                //analysisView.showAnalysis(activeHabits, completedHabits)
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
}
