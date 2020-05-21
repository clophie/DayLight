package com.daylight.statistics

import com.daylight.data.habits.Habit
import com.daylight.data.habits.HabitsDataSource
import com.daylight.data.habits.HabitsRepository


/**
 * Listens to user actions from the UI ([StatisticsFragment]), retrieves the data and updates
 * the UI as required.
 */
class StatisticsPresenter(
    val habitsRepository: HabitsRepository,
    val statisticsView: StatisticsContract.View
) : StatisticsContract.Presenter {

    init {
        statisticsView.presenter = this
    }

    override fun start() {
        loadStatistics()
    }

    private fun loadStatistics() {
        statisticsView.setProgressIndicator(true)

        habitsRepository.getHabits(object : HabitsDataSource.LoadHabitsCallback {
            override fun onHabitsLoaded(habits: List<Habit>) {
                // We calculate number of active and completed habits

                // The view may not be able to handle UI updates anymore
                if (!statisticsView.isActive) {
                    return
                }
                statisticsView.setProgressIndicator(false)
                //statisticsView.showStatistics(activeHabits, completedHabits)
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!statisticsView.isActive) {
                    return
                }
                statisticsView.showLoadingStatisticsError()
            }
        })
    }
}
