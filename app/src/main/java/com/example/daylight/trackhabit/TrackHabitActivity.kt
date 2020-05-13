package com.example.daylight.trackhabit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daylight.R
import com.example.daylight.data.source.HabitsRepository
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.HabitsLocalDataSource
import com.example.daylight.util.AppExecutors
import com.example.daylight.util.replaceFragmentInActivity

class TrackHabitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.trackhabit_act)

        val trackHabitFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as TrackHabitFragment? ?:
        TrackHabitFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

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