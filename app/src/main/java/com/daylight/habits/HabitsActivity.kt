package com.daylight.habits

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.daylight.R
import com.daylight.analysis.AnalysisFragment
import com.daylight.appintro.DayLightAppIntro
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.DaylightDatabase
import com.daylight.data.habits.HabitsLocalDataSource
import com.daylight.moods.MoodsFragment
import com.daylight.settings.SettingsFragment
import com.daylight.util.AppExecutors
import com.daylight.util.notif.AlarmScheduler
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.habits_act.*
import java.util.*


class HabitsActivity : AppCompatActivity() {

    private lateinit var habitsPresenter: HabitsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.habits_act)

        val settings: SharedPreferences? = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        if (settings!!.getBoolean("first_launch", true)) {
            // Set up mood tracking and correlation alarms
            val alarmManager = applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val timeToAlarm = Calendar.getInstance()
            timeToAlarm.set(Calendar.HOUR_OF_DAY, 20)
            timeToAlarm.set(Calendar.MINUTE, 0)
            applicationContext?.let { AlarmScheduler.scheduleMoodAlarm(it, alarmManager, timeToAlarm)
                AlarmScheduler.scheduleCorrelationAlarm(it, alarmManager) }

            // Play app intro
            val intent = Intent(this, DayLightAppIntro::class.java).apply {}
            startActivity(intent)

            // Record the fact that the app has been started at least once
            settings.edit().putBoolean("first_launch", false).apply()
        }

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

                R.id.navigation_settings-> {
                    toolbar!!.title = resources.getString(R.string.settings)
                    val fragment = SettingsFragment()
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
        habitsPresenter = HabitsPresenter(repo, habitsFragment)
    }
}
