package com.daylight.addeditmood

import com.daylight.BasePresenter
import com.daylight.BaseView


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

        fun saveMood(name: String, score: Int)

        fun populateMood()
    }
}
