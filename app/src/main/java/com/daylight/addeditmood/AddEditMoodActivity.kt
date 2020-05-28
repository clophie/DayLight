package com.daylight.addeditmood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daylight.R
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar


/**
 * Displays an add or edit mood screen.
 */
class AddEditMoodActivity : AppCompatActivity() {

    private lateinit var addEditMoodPresenter: AddEditMoodPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addmood_act)
        val moodId = intent.getStringExtra(AddEditMoodFragment.ARGUMENT_EDIT_MOOD_ID)


        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(if (moodId == null) R.string.add_mood else R.string.edit_mood)
        }

        val addEditMoodFragment =
            supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditMoodFragment?
                ?: AddEditMoodFragment.newInstance(moodId).also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

        val shouldLoadDataFromRepo =
        // Prevent the presenter from loading data from the repository if this is a config change.
            // Data might not have loaded when the config change happen, so we saved the state.
            savedInstanceState?.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY) ?: true

        // Create the presenter
        val database = DaylightDatabase.getInstance(applicationContext)

        addEditMoodPresenter = AddEditMoodPresenter(moodId,
            MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()), addEditMoodFragment,
            shouldLoadDataFromRepo)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the state so that next time we know if we need to refresh data.
        super.onSaveInstanceState(outState.apply {
            putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, addEditMoodPresenter.isDataMissing)
        })
    }

    companion object {
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"
        const val REQUEST_ADD_MOOD = 1
    }
}