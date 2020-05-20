package com.example.daylight.moods

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.daylight.R
import com.example.daylight.data.source.local.DaylightDatabase
import com.example.daylight.data.source.local.moods.MoodsLocalDataSource
import com.example.daylight.data.source.moods.MoodsRepository
import com.example.daylight.habits.HabitsFragment
import com.example.daylight.statistics.StatisticsFragment
import com.example.daylight.util.AppExecutors
import com.example.daylight.util.replaceFragmentInActivity
import com.example.daylight.util.setupActionBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.habits_act.*

class MoodsActivity : AppCompatActivity() {

    private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"

    private lateinit var moodsPresenter: MoodsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moods_act)

        if (intent.extras !== null) {
            val rootView: View = window.decorView.rootView
            val snackBar = Snackbar.make(rootView.findViewById(R.id.contentFrame), intent.extras!!["SNACKBAR_CONTENT"].toString(), Snackbar.LENGTH_LONG)
            snackBar.show()
        }

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayShowTitleEnabled(true)
            title = resources.getString(R.string.moods)
        }

        // Set up the bottom navigation.
        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_habits-> {
                    title=resources.getString(R.string.moods)
                    val fragment = HabitsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                //TODO Change this to the moods fragment
                R.id.navigation_moods-> {
                    title=resources.getString(R.string.moods)
                    val fragment = MoodsFragment()
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

        val moodsFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as MoodsFragment? ?: MoodsFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        //Get the database and repo
        val database = DaylightDatabase.getInstance(applicationContext)
        val repo = MoodsRepository.getInstance(
            MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

        // Create the presenter
        moodsPresenter = MoodsPresenter(repo,
            moodsFragment).apply {
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putSerializable(CURRENT_FILTERING_KEY, moodsPresenter.currentFiltering)
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