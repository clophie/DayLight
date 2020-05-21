package com.daylight.addeditmood

import android.content.Context
import com.daylight.BasePresenter
import com.daylight.BaseView


interface AddEditMoodContract {

    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun showEmptyMoodError()

        fun showMoodsList()

        fun setName(title: String)

        fun setScore(score: Int)

        fun setImage(image: String)
    }

    interface Presenter : BasePresenter {
        var isDataMissing: Boolean

        fun saveMood(name: String, score: Int, image: String, context: Context)

        fun populateMood()
    }
}
