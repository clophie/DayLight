package com.example.daylight.trackhabit

import com.example.daylight.data.source.HabitsRepository

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


}