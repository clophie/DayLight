package com.example.daylight.trackhabit

import com.example.daylight.data.source.Habit
import com.example.daylight.data.source.HabitsDataSource
import com.example.daylight.data.source.HabitsRepository
import com.example.daylight.habits.HabitsFilterType
import java.util.ArrayList

/**
 * Listens to user actions from the UI ([TrackHabitFragment]), retrieves the data and updates
 * the UI as required.
 */
class TrackHabitPresenter(
    private val habitsRepository: HabitsRepository,
    private val trackHabitView: TrackHabitContract.View
) : TrackHabitContract.Presenter {

    init {
        trackHabitView.presenter = this
    }

    override fun submitTracking() {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun loadHabits() {
        habitsRepository.getHabits(object : HabitsDataSource.LoadHabitsCallback {
            override fun onHabitsLoaded(habits: List<Habit>) {
                trackHabitView.populateSpinner(habits)
            }

            override fun onDataNotAvailable() {
                return
            }
        })
    }


}