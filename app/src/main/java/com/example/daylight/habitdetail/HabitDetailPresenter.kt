package com.example.daylight.habitdetail

import com.example.daylight.data.source.Habit
import com.example.daylight.data.source.HabitTracking
import com.example.daylight.data.source.HabitsDataSource
import com.example.daylight.data.source.HabitsRepository


/**
 * Listens to user actions from the UI ([HabitDetailFragment]), retrieves the data and updates
 * the UI as required.
 */
class HabitDetailPresenter(
    private val habitId: String,
    private val habitsRepository: HabitsRepository,
    private val habitDetailView: HabitDetailContract.View
) : HabitDetailContract.Presenter {

    init {
        habitDetailView.presenter = this
    }

    override fun start() {
        openHabit()
    }

    private fun openHabit() {
        if (habitId.isEmpty()) {
            habitDetailView.showMissingHabit()
            return
        }

        habitDetailView.setLoadingIndicator(true)
        habitsRepository.getHabit(habitId, object : HabitsDataSource.GetHabitCallback {
            override fun onHabitLoaded(habit: Habit) {
                with(habitDetailView) {
                    // The view may not be able to handle UI updates anymore
                    if (!isActive) {
                        return@onHabitLoaded
                    }
                    setLoadingIndicator(false)
                }
                showHabit(habit)
            }

            override fun onDataNotAvailable() {
                with(habitDetailView) {
                    // The view may not be able to handle UI updates anymore
                    if (!isActive) {
                        return@onDataNotAvailable
                    }
                    showMissingHabit()
                }
            }
        })

        habitsRepository.getHabitTrackingByHabitId(habitId, object : HabitsDataSource.GetHabitTrackingCallback {
            override fun onHabitTrackingLoaded(habitTracking: List<HabitTracking>) {
                with(habitDetailView) {
                    setLoadingIndicator(false)
                }
                showHabitTracking(habitTracking)
            }

            override fun onDataNotAvailable() {
                with(habitDetailView) {
                    // The view may not be able to handle UI updates anymore
                    if (!isActive) {
                        return@onDataNotAvailable
                    }
                }
            }

        })
    }

    override fun editHabit() {
        if (habitId.isEmpty()) {
            habitDetailView.showMissingHabit()
            return
        }
        habitDetailView.showEditHabit(habitId)
    }

    override fun deleteHabit() {
        if (habitId.isEmpty()) {
            habitDetailView.showMissingHabit()
            return
        }
        habitsRepository.deleteHabit(habitId)
        habitDetailView.showHabitDeleted()
    }

    private fun showHabit(habit: Habit) {
        with(habitDetailView) {
            if (habitId.isEmpty()) {
                hideTitle()
                hideDescription()
            } else {
                showTitle(habit.title)
                showDescription(habit.description)
            }
        }
    }

    private fun showHabitTracking(habitTracking: List<HabitTracking>) {
        with(habitDetailView) {
            showHabitTracking(habitTracking)
        }
    }
}
