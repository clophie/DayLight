package com.example.daylight.habits

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.test.espresso.IdlingResource
import com.example.daylight.R
import com.example.daylight.data.source.HabitsRepository
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.HabitsLocalDataSource
import com.example.daylight.statistics.StatisticsFragment
import com.example.daylight.util.AppExecutors
import com.example.daylight.util.replaceFragmentInActivity
import com.example.daylight.util.setupActionBar
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.habits_act.*

class HabitsActivity : AppCompatActivity() {

    private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var habitsPresenter: HabitsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.habits_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        // Set up the bottom navigation.
        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_habits-> {
                    title=resources.getString(R.string.habits)
                    loadFragment(HabitsFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                //TODO Change this to the moods fragment
                R.id.navigation_moods-> {
                    title=resources.getString(R.string.moods)
                    loadFragment(HabitsFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_analysis-> {
                    title=resources.getString(R.string.analysis)
                    loadFragment(StatisticsFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                //TODO Change this to the settings fragment
                R.id.navigation_settings-> {
                    title=resources.getString(R.string.settings)
                    loadFragment(HabitsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        val habitsFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as HabitsFragment? ?: HabitsFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        // Create the presenter
        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao()))

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Open the navigation drawer when the home icon is selected from the toolbar.
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
