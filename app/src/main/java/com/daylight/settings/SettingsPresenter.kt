package com.daylight.settings

import com.daylight.data.habits.HabitsRepository
import com.daylight.data.moods.MoodsRepository

class SettingsPresenter(
    val habitsRepository: HabitsRepository,
    val moodsRepository: MoodsRepository,
    val settingsView: SettingsContract.View

) : SettingsContract.Presenter {

    init {
        settingsView.presenter = this
    }

    override fun start() { }

}