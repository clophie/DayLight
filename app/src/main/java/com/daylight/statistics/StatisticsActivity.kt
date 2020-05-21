package com.daylight.statistics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daylight.R
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar


/**
 * Show statistics for habits.
 */
class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.statistics_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setTitle(R.string.statistics_title)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        val statisticsFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as StatisticsFragment?
            ?: StatisticsFragment.newInstance().also {
                replaceFragmentInActivity(it, R.id.contentFrame)
            }
        
        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = HabitsRepository.getInstance(
            HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))
        
        StatisticsPresenter(repo, statisticsFragment)
    }
}
