package com.daylight.moods

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.daylight.R
import com.daylight.analysis.AnalysisActivity
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.habits.HabitsActivity
import com.daylight.util.AppExecutors
import com.daylight.util.replaceFragmentInActivity
import com.daylight.util.setupActionBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.habits_act.*

class MoodsActivity : AppCompatActivity() {

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
        val toolbar = supportActionBar

        // Set up the bottom navigation.
        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_habits-> {
                    toolbar!!.title = resources.getString(R.string.habits)
                    val intent = Intent(this, HabitsActivity::class.java)
                    startActivity(intent)
                }

                R.id.navigation_moods-> {
                    toolbar!!.title = resources.getString(R.string.moods)
                    val intent = Intent(this, MoodsActivity::class.java)
                    startActivity(intent)
                }

                R.id.navigation_analysis-> {
                    toolbar!!.title = resources.getString(R.string.analysis)
                    val intent = Intent(this, AnalysisActivity::class.java)
                    startActivity(intent)
                }

                //TODO Change this to the settings fragment
                R.id.navigation_settings-> {
                    toolbar!!.title = resources.getString(R.string.settings)
                    val intent = Intent(this, HabitsActivity::class.java)
                    startActivity(intent)
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

}
