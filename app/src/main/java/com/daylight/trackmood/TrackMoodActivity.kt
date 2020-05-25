package com.daylight.trackmood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daylight.R
import com.daylight.data.moods.MoodsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar


class TrackMoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.trackmood_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            title = resources.getString(R.string.trackMoodFab)
        }

        val trackMoodFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as TrackMoodFragment? ?:
        TrackMoodFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = MoodsRepository.getInstance(
            MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

        TrackMoodPresenter(repo, trackMoodFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val REQUEST_TRACK_MOOD = 1
    }
}