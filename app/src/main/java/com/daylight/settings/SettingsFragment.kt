package com.daylight.settings

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import com.daylight.util.notif.AlarmScheduler
import java.util.*


class SettingsFragment : Fragment(), SettingsContract.View {

    lateinit var notificationSettingsButton : Button
    lateinit var replayTutorialButton : Button
    lateinit var timeButton: Button

    override lateinit var presenter: SettingsContract.Presenter

    override val isActive: Boolean
        get() = isAdded
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.settings_frag, container, false)

        with(root) {
            notificationSettingsButton = findViewById(R.id.notificationSettingsButton)
            replayTutorialButton = findViewById(R.id.replayTutorialButton)
            timeButton = findViewById(R.id.moodNotifTimeButton)
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
            intent.putExtra("app_package", context?.packageName)
            intent.putExtra("android.provider.extra.APP_PACKAGE", context?.packageName)

            startActivity(intent)
        }

        replayTutorialButton.setOnClickListener {
            val intent = Intent(context, DayLightAppIntro::class.java).apply {}
            startActivity(intent)
        }

        // Set up date and time pop ups
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        timeButton.setOnClickListener {
            val tpd = TimePickerDialog(activity!!,
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)

                    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    context?.let { AlarmScheduler.scheduleMoodAlarm(it, alarmManager, c) }

                }, hour, minute, false
            )

            tpd.show()
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