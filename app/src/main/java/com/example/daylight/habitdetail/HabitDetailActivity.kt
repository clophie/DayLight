package com.example.daylight.habitdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daylight.R
import com.example.daylight.data.source.habits.HabitsRepository
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.habits.HabitsLocalDataSource
import com.example.daylight.util.AppExecutors
import com.example.daylight.util.replaceFragmentInActivity
import com.example.daylight.util.setupActionBar


/**
 * Displays habit details screen.
 */
class HabitDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.habitdetail_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Get the requested habit id
        val habitId = intent.getStringExtra(EXTRA_HABIT_ID)

        val habitDetailFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as HabitDetailFragment? ?:
        HabitDetailFragment.newInstance(habitId).also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = HabitsRepository.getInstance(
            HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

        // Create the presenter
        HabitDetailPresenter(habitId, repo,
            habitDetailFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_HABIT_ID = "HABIT_ID"
    }
}
