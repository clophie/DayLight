package com.daylight.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daylight.R
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.DaylightDatabase
import com.daylight.data.habits.HabitsLocalDataSource
import com.daylight.data.moods.MoodsLocalDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setTitle(R.string.settings_title)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        val settingsFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as SettingsFragment?
            ?: SettingsFragment.newInstance().also {
                replaceFragmentInActivity(it, R.id.contentFrame)
            }

        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = HabitsRepository.getInstance(
            HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

        val moodsRepo = MoodsRepository.getInstance(
            MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

        SettingsPresenter(repo, moodsRepo, settingsFragment)
    }
}
