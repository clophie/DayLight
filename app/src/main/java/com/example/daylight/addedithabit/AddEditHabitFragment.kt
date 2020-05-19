package com.example.daylight.addedithabit

import android.app.Activity
import android.app.NotificationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.daylight.R
import com.example.daylight.util.notif.sendNotification
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


/**
 * Main UI for the add habit screen. Users can enter a habit title, description, target day and target time.
 */
class AddEditHabitFragment : Fragment(), AddEditHabitContract.View {

    override lateinit var presenter: AddEditHabitContract.Presenter
    override var isActive = false
        get() = isAdded

    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var days: MaterialDayPicker
    private lateinit var time: TimePicker


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_habit_done)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                var c = Calendar.getInstance()

                time.setIs24HourView(true) //set Timer to 24 hours Format

                c.set(Calendar.HOUR, time.hour)
                c.set(Calendar.HOUR_OF_DAY, time.hour)
                c.set(Calendar.MINUTE, time.minute)
                presenter.saveHabit(title.text.toString(), description.text.toString(), days.selectedDays.toMutableList(), c, context)

                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(context.getString(R.string.app_name), context)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addhabit_frag, container, false)
        with(root) {
            title = findViewById(R.id.add_habit_title)
            description = findViewById(R.id.add_habit_description)
            days =  findViewById(R.id.habit_day_picker)
            time = findViewById(R.id.habit_time_picker)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun showEmptyHabitError() {
    }

    override fun showHabitsList() {
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun setTitle(title: String) {
        this.title.text = title
    }

    override fun setDescription(description: String) {
        this.description.text = description
    }

    override fun setDays(days: List<MaterialDayPicker.Weekday>) {
        this.days.setSelectedDays(days)
    }

    override fun setTime(time: Calendar) {
        this.time.hour = time.get(Calendar.HOUR_OF_DAY)
        this.time.minute = time.get(Calendar.MINUTE)
    }

    companion object {
        const val ARGUMENT_EDIT_HABIT_ID = "EDIT_HABIT_ID"

        fun newInstance(habitId: String?) =
            AddEditHabitFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGUMENT_EDIT_HABIT_ID, habitId)
                }
            }
    }
}
