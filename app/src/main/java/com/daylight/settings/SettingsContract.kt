package com.daylight.settings

import com.daylight.BasePresenter
import com.daylight.BaseView

interface SettingsContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {

    }
}