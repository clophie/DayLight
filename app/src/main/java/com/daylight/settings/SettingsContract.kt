package com.daylight.settings

import com.daylight.BasePresenter
import com.daylight.BaseView

interface SettingsContract {

    interface View : BaseView<Presenter> {
        val isActive: Boolean
    }

    interface Presenter : BasePresenter {

    }
}