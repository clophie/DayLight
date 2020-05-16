package com.example.daylight.trackhabit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.daylight.R
import com.example.daylight.data.source.Habit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS


/**
 * Main UI for the track habit screen.
 */
class TrackHabitFragment : Fragment(), TrackHabitContract.View {

    private lateinit var habitSpinner: Spinner
    private lateinit var dateField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeField: EditText
    private lateinit var timeButton: Button

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

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_habit_done)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                presenter.submitTracking()
            }
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)

        dateField.setText("$day/$month/$year")

        dateButton.setOnClickListener {
            val dpd = DatePickerDialog(activity!!, OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                dateField.setText("$dayOfMonth/$monthOfYear/$year")

            }, year, month, day)

            dpd.show()
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
            habitTitles)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        habitSpinner.adapter = adapter
    }

    companion object {

        fun newInstance() =
            TrackHabitFragment().apply {
            }
    }
}