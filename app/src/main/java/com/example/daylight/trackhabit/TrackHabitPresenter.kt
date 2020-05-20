package com.example.daylight.trackhabit

import com.example.daylight.data.source.habits.Habit
import com.example.daylight.data.source.habits.HabitTracking
import com.example.daylight.data.source.habits.HabitsDataSource
import com.example.daylight.data.source.habits.HabitsRepository
import java.util.*

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

    override fun submitTracking(habitid: String, completionDateTime: Calendar) {
        val habitTracking =
            HabitTracking(
                completionDateTime,
                habitid,
                Calendar.getInstance()
            )
        habitsRepository.insertHabitTracking(habitTracking)
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