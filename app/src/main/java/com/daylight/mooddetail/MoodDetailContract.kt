package com.daylight.mooddetail

import com.daylight.BasePresenter
import com.daylight.BaseView
import com.daylight.data.moods.MoodTracking
import java.util.*


/**
 * This specifies the contract between the view and the presenter.
 */
interface MoodDetailContract {

    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showMissingMood()

        fun hideTitle()

        fun showTitle(title: String)

        fun showEditMood(moodId: String)

        fun showMoodDeleted()

        fun showMoodTracking(moodTracking: List<MoodTracking>)

    }

    interface Presenter : BasePresenter {

        fun editMood()

        fun deleteMood()

        fun deleteMoodTracking(date: Calendar)

        fun loadMoodTracking()
    }
}
