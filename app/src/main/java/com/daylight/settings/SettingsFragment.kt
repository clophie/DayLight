package com.daylight.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.daylight.R
import com.daylight.appintro.DayLightAppIntro
import com.daylight.data.habits.HabitsRepository
import com.daylight.data.local.DaylightDatabase
import com.daylight.data.local.habits.HabitsLocalDataSource
import com.daylight.data.local.moods.MoodsLocalDataSource
import com.daylight.data.moods.MoodsRepository
import com.daylight.util.AppExecutors


class SettingsFragment : Fragment(), SettingsContract.View {

    lateinit var notificationSettingsButton : Button
    lateinit var replayTutorialButton : Button

    override lateinit var presenter: SettingsContract.Presenter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.settings_frag, container, false)

        with(root) {
            notificationSettingsButton = findViewById(R.id.notificationSettingsButton)
            replayTutorialButton = findViewById(R.id.replayTutorialButton)
        }

        if (!this::presenter.isInitialized) {
            val database = DaylightDatabase.getInstance(getActivity()!!.getApplicationContext())
            val repo = HabitsRepository.getInstance(
                HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

            val moodsRepo = MoodsRepository.getInstance(
                MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

            SettingsPresenter(repo, moodsRepo, this)
        }

        presenter.start()

        notificationSettingsButton.setOnClickListener {
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context?.getPackageName())
            intent.putExtra("android.provider.extra.APP_PACKAGE", context?.getPackageName())

            startActivity(intent)
        }

        replayTutorialButton.setOnClickListener {
            val intent = Intent(context, DayLightAppIntro::class.java).apply {}
            startActivity(intent)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (!this::presenter.isInitialized) {
            val database = DaylightDatabase.getInstance(getActivity()!!.getApplicationContext())
            val repo = HabitsRepository.getInstance(
                HabitsLocalDataSource.getInstance(AppExecutors(), database.habitDao(), database.habitTrackingDao()))

            val moodsRepo = MoodsRepository.getInstance(
                MoodsLocalDataSource.getInstance(AppExecutors(), database.moodDao(), database.moodTrackingDao()))

            SettingsPresenter(repo, moodsRepo, this)
        }

        presenter.start()
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}