package com.example.daylight.habits

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.daylight.R
import com.example.daylight.data.source.HabitsRepository
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.HabitsLocalDataSource
import com.example.daylight.statistics.StatisticsFragment
import com.example.daylight.util.AppExecutors
import com.example.daylight.util.replaceFragmentInActivity
import com.example.daylight.util.setupActionBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.habits_act.*


class HabitsActivity : AppCompatActivity() {

    private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"

    private lateinit var habitsPresenter: HabitsPresenter

    // Boolean flag to know if main FAB is in open or closed state.
    private var fabExpanded = false
    private var mainFab: FloatingActionButton? = null

    // Linear layout holding the Save submenu
    private var layoutTrackHabitFab: LinearLayout? = null

    // Linear layout holding the Edit submenu
    private var layoutNewHabitFab: LinearLayout? = null
    private var layoutFabPhoto: LinearLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.habits_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            title = resources.getString(R.string.habits)
        }

        mainFab = findViewById(R.id.fab_add_habit)

        layoutTrackHabitFab = findViewById(R.id.fab_track_habit_layout)
        layoutNewHabitFab = findViewById(R.id.fab_new_habit_layout)

        //When main Fab (Settings) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Settings) open/close behavior

        //When main Fab (Settings) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Settings) open/close behavior
        mainFab!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (fabExpanded) {
                    closeSubMenusFab()
                } else {
                    openSubMenusFab()
                }
            }
        })

        //Only main FAB is visible in the beginning
        closeSubMenusFab()

        // Set up the bottom navigation.
        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_habits-> {
                    title=resources.getString(R.string.habits)
                    val fragment = HabitsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                //TODO Change this to the moods fragment
                R.id.navigation_moods-> {
                    title=resources.getString(R.string.moods)
                    val fragment = HabitsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_analysis-> {
                    title=resources.getString(R.string.analysis)
                    val fragment = StatisticsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                //TODO Change this to the settings fragment
                R.id.navigation_settings-> {
                    title=resources.getString(R.string.settings)
                    val fragment = HabitsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
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
        val repo = HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

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

    //closes FAB submenus
    private fun closeSubMenusFab() {
        layoutTrackHabitFab!!.visibility = View.INVISIBLE
        layoutNewHabitFab!!.visibility = View.INVISIBLE
        mainFab!!.setImageResource(R.drawable.ic_add)
        fabExpanded = false
    }

    //Opens FAB submenus
    private fun openSubMenusFab() {
        layoutTrackHabitFab!!.visibility = View.VISIBLE
        layoutNewHabitFab!!.visibility = View.VISIBLE

        //Change settings icon to 'X' icon
        mainFab!!.setImageResource(R.drawable.ic_close_white_24dp)
        fabExpanded = true
    }
}
