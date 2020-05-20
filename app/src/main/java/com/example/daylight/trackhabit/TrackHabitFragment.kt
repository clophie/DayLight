package com.example.daylight.trackhabit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import com.example.daylight.R
import com.example.daylight.data.source.habits.Habit
import com.example.daylight.habits.HabitsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


/**
 * Main UI for the track habit screen.
 */
class TrackHabitFragment : Fragment(), TrackHabitContract.View {

    private lateinit var habitSpinner: Spinner
    private lateinit var dateField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeField: EditText
    private lateinit var timeButton: Button

    private lateinit var selectedHabitId: String

    override lateinit var presenter: TrackHabitContract.Presenter

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.trackhabit_frag, container, false)
        setHasOptionsMenu(true)
        with(root) {
            habitSpinner = findViewById(R.id.habit_spinner)
            dateField = findViewById(R.id.dateField)
            dateButton = findViewById(R.id.dateButton)
            timeField = findViewById(R.id.timeField)
            timeButton = findViewById(R.id.timeButton)
        }

        // Set up date and time pop ups
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        dateField.setText("$day/$month/$year")
        timeField.setText(String.format("%02d:%02d", hour, minute))

        dateButton.setOnClickListener {
            val dpd =
                DatePickerDialog(activity!!, OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                    // Display Selected date in textbox
                    dateField.setText("$dayOfMonth/$monthOfYear/$year")

                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                }, year, month, day)

            dpd.show()
        }

        timeButton.setOnClickListener {
            val tpd = TimePickerDialog(activity!!,
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->

                    // Display Selected date in textbox
                    timeField.setText(String.format("%02d:%02d", hour, minute))

                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)

                }, hour, minute, false
            )

            tpd.show()
        }

        // Set up floating action button
        activity!!.findViewById<FloatingActionButton>(R.id.fab_confirm_track_habit)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                c.set(Calendar.SECOND, 0)
                c.set(Calendar.MILLISECOND, 0)
                presenter.submitTracking(selectedHabitId, c)

                // Redirect back to the habits screen
                val intent = Intent(context, HabitsActivity::class.java)
                intent.putExtra("SNACKBAR_CONTENT", "Habit Tracked!")
                startActivity(intent)
            }
        }

        presenter.loadHabits()

        return root
    }

    override fun populateSpinner(habits: List<Habit>) {
        val habitTitles = habits.map { it.title }

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_item,
            habitTitles
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Add on selected listener to store the habitid of the currently selected habit
        habitSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                pos: Int,
                id: Long
            ) {
                selectedHabitId = habits[pos].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Apply the adapter to the spinner
        habitSpinner.adapter = adapter
    }

    companion object {

        fun newInstance() =
            TrackHabitFragment().apply {
            }
    }
}