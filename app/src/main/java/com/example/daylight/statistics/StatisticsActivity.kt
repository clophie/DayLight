package com.example.daylight.statistics

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.daylight.R
import com.example.daylight.data.source.HabitsRepository
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.HabitsLocalDataSource
import com.example.daylight.util.AppExecutors
import com.example.daylight.util.replaceFragmentInActivity
import com.example.daylight.util.setupActionBar
import com.google.android.material.navigation.NavigationView


/**
 * Show statistics for habits.
 */
class StatisticsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.statistics_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setTitle(R.string.statistics_title)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        // Set up the navigation drawer.
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        setupDrawerContent(navigationView)

        val statisticsFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as StatisticsFragment?
            ?: StatisticsFragment.newInstance().also {
                replaceFragmentInActivity(it, R.id.contentFrame)
            }
        
        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao()))
        
        StatisticsPresenter(repo, statisticsFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Open the navigation drawer when the home icon is selected from the toolbar.
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.list_navigation_menu_item) {
                NavUtils.navigateUpFromSameTask(this@StatisticsActivity)
            }
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }
}
