package com.daylight.trackmood

import com.daylight.data.moods.Mood
import com.daylight.data.moods.MoodTracking
import com.daylight.data.moods.MoodsDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.trackmood.TrackMoodContract
import com.daylight.trackmood.TrackMoodFragment
import java.util.*


/**
 * Listens to user actions from the UI ([TrackMoodFragment]), retrieves the data and updates
 * the UI as required.
 */
class TrackMoodPresenter(
    private val moodsRepository: MoodsRepository,
    private val trackMoodView: TrackMoodContract.View
) : TrackMoodContract.Presenter {

    init {
        trackMoodView.presenter = this
    }

    override fun submitTracking(moodId: String, completionDateTime: Calendar) {
        val moodTracking =
            MoodTracking(
                moodId,
                completionDateTime
            )
        moodsRepository.insertMoodTracking(moodTracking)
    }

    override fun start() { }

    override fun loadMoods() {
        moodsRepository.getMoods(object : MoodsDataSource.LoadMoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                trackMoodView.populateSpinner(moods)
            }

            override fun onDataNotAvailable() {
                return
            }
        })
    }


}