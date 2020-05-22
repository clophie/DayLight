package com.daylight.addeditmood

import android.content.Context
import android.graphics.drawable.Drawable
import com.daylight.BasePresenter
import com.daylight.BaseView
import com.daylight.data.moods.Mood


interface AddEditMoodContract {

    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun showEmptyMoodError()

        fun showMoodsList()

        fun setName(name: String)

        fun setScore(score: Int)
    }

    interface Presenter : BasePresenter {
        var isDataMissing: Boolean

        fun saveMood(name: String, score: Int, context: Context)

        fun populateMood()
    }
}
