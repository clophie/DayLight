package com.example.daylight.addedithabit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daylight.R
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.HabitsDao
import com.example.daylight.data.source.local.HabitsLocalDataSource
import com.example.daylight.util.AppExecutors
import com.example.daylight.util.replaceFragmentInActivity
import com.example.daylight.util.setupActionBar


/**
 * Displays an add or edit habit screen.
 */
class AddEditHabitActivity : AppCompatActivity() {

    private lateinit var addEditHabitPresenter: AddEditHabitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addhabit_act)
        val habitId = intent.getStringExtra(AddEditHabitFragment.ARGUMENT_EDIT_HABIT_ID)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(if (habitId == null) R.string.add_habit else R.string.edit_habit)
        }

        val addEditHabitFragment =
            supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditHabitFragment?
                ?: AddEditHabitFragment.newInstance(habitId).also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

        val shouldLoadDataFromRepo =
        // Prevent the presenter from loading data from the repository if this is a config change.
            // Data might not have loaded when the config change happen, so we saved the state.
            savedInstanceState?.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY) ?: true

        // Create the presenter
        val database = DaylightDatabase.getInstance(applicationContext)

        addEditHabitPresenter = AddEditHabitPresenter(habitId,
            HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao()), addEditHabitFragment,
            shouldLoadDataFromRepo)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the state so that next time we know if we need to refresh data.
        super.onSaveInstanceState(outState.apply {
            putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, addEditHabitPresenter.isDataMissing)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"
        const val REQUEST_ADD_HABIT = 1
    }
}