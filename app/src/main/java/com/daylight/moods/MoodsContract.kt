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

        fun showTrackMood()

        fun showMoodDetailsUi(moodId: String)

        fun showMoodMarkedComplete()

        fun showMoodMarkedActive()

        fun showCompletedMoodsCleared()

        fun showLoadingMoodsError()

        fun showNoMoods()

        fun showSuccessfullySavedMessage()
    }

    interface Presenter : BasePresenter {


        fun result(requestCode: Int, resultCode: Int)

        fun loadMoods(forceUpdate: Boolean)

        fun addNewMood()

        fun trackMood()

        fun openMoodDetails(requestedMood: Mood)
    }
}