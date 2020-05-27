package com.daylight.habits

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.daylight.R
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.moods.MoodsFragment
import com.daylight.analysis.AnalysisFragment
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.habit_item.view.*
import kotlinx.android.synthetic.main.habits_act.*


class HabitsActivity : AppCompatActivity() {

    private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"

    private lateinit var habitsPresenter: HabitsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.habits_act)

        if (intent.extras !== null) {
            val rootView: View = window.decorView.rootView
            val snackBar = Snackbar.make(rootView.findViewById(R.id.contentFrame), intent.extras!!["SNACKBAR_CONTENT"].toString(), Snackbar.LENGTH_LONG)
            snackBar.show()
        }

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            title = resources.getString(R.string.habits)
        }
        val toolbar = supportActionBar

        // Set up the bottom navigation.
        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_habits-> {
                    toolbar!!.title = resources.getString(R.string.habits)
                    val fragment = HabitsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.contentFrame, fragment)
                        .commit()
                    findViewById<FloatingActionMenu>(R.id.main_fab).visibility = View.VISIBLE
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_moods-> {
                    toolbar!!.title = resources.getString(R.string.moods)
                    val fragment = MoodsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.contentFrame, fragment)
                        .commit()
                    findViewById<FloatingActionMenu>(R.id.main_fab).visibility = View.VISIBLE
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_analysis-> {
                    toolbar!!.title = resources.getString(R.string.analysis)
                    val fragment = AnalysisFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.contentFrame, fragment)
                        .commit()
                    findViewById<FloatingActionMenu>(R.id.main_fab).visibility = View.INVISIBLE
                    return@setOnNavigationItemSelectedListener true
                }

                //TODO Change this to the settings fragment
                R.id.navigation_settings-> {
                    toolbar!!.title = resources.getString(R.string.settings)
                    val fragment = HabitsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.contentFrame, fragment)
                        .commit()
                    findViewById<FloatingActionMenu>(R.id.main_fab).visibility = View.INVISIBLE
                    return@setOnNavigationItemSelectedListener true
                }
            }

            false
        }

        val habitsFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as HabitsFragment? ?: HabitsFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        //Get the database and repo
        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = HabitsRepository.getInstance(
            HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

        // Create the presenter
        habitsPresenter = HabitsPresenter(repo,
            habitsFragment).apply {
            // Load previously saved state, if available.
            if (savedInstanceState != null) {
                currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_KEY)
                        as HabitsFilterType
            }
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putSerializable(CURRENT_FILTERING_KEY, habitsPresenter.currentFiltering)
        })
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
