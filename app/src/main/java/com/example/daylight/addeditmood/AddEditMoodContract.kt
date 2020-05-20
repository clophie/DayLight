package com.example.daylight.addeditmood

import android.content.Context
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.daylight.BasePresenter
import com.example.daylight.BaseView
import java.util.*


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
