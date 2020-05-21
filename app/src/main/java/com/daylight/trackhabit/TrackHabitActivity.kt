package com.daylight.trackhabit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daylight.R
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar

class TrackHabitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.trackhabit_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            title = resources.getString(R.string.trackHabitFab)
        }

        val trackHabitFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as TrackHabitFragment? ?:
        TrackHabitFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = HabitsRepository.getInstance(
            HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

        TrackHabitPresenter(repo, trackHabitFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val REQUEST_TRACK_HABIT = 1
    }
}