package com.daylight.trackmood

import com.daylight.BasePresenter
import com.daylight.BaseView
import com.daylight.data.moods.Mood
import java.util.*

interface TrackMoodContract {

    interface View : BaseView<Presenter> {
        fun populateSpinner(moods: List<Mood>)
    }

    interface Presenter : BasePresenter {

        fun submitTracking(moodName: String, completionDateTime: Calendar)

        fun loadMoods()
    }
}