package com.daylight.moods

import com.daylight.BasePresenter
import com.daylight.BaseView
import com.daylight.data.moods.Mood


/**
 * This specifies the contract between the view and the presenter.
 */
interface  MoodsContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showMoods(moods: List<Mood>)

        fun showAddMood()

        fun showAddHabit()

        fun showTrackMood()

        fun showTrackHabit()

        fun showMoodDetailsUi(moodId: String)

        fun showLoadingMoodsError()

        fun showNoMoods()

        fun showSuccessfullySavedMessage()

        fun showConfirmDelete(requestedMood: Mood)
    }

    interface Presenter : BasePresenter {

        fun result(requestCode: Int, resultCode: Int)

        fun loadMoods(forceUpdate: Boolean)

        fun confirmDelete(requestedMood: Mood)

        fun deleteMood(requestedMood: Mood)

        fun addNewMood()

        fun addNewHabit()

        fun trackMood()

        fun trackHabit()

        fun openMoodDetails(requestedMood: Mood)
    }
}