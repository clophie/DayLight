package com.daylight.mooddetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daylight.R
import com.daylight.data.moods.MoodsRepository
import com.daylight.data.DaylightDatabase
import com.daylight.data.moods.MoodsLocalDataSource
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar


/**
 * Displays mood details screen.
 */
class MoodDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.mooddetail_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Get the requested mood id
        val moodId = intent.getStringExtra(EXTRA_MOOD_ID)
        val moodName = intent.getStringExtra(EXTRA_MOOD_NAME)

        val moodDetailFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as MoodDetailFragment? ?:
        MoodDetailFragment.newInstance(moodId).also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = MoodsRepository.getInstance(
            MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

        // Create the presenter
        MoodDetailPresenter(moodId, moodName, repo,
            moodDetailFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_MOOD_ID = "MOOD_ID"
        const val EXTRA_MOOD_NAME = "MOOD_NAME"
    }
}
