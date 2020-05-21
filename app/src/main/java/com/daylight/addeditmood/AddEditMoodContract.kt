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

        fun setIcon(icon: String)

        fun setIcon(icon: Drawable)
    }

    interface Presenter : BasePresenter {
        var isDataMissing: Boolean

        fun setIcon(icon: Drawable)

        fun saveMood(name: String, score: Int, image: Drawable, context: Context)

        fun populateMood()
    }
}
