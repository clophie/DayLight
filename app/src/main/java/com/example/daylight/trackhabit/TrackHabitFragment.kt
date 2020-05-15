package com.example.daylight.trackhabit

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


/**
 * Main UI for the track habit screen.
 */
class TrackHabitFragment : Fragment(), TrackHabitContract.View {

    private lateinit var habitSpinner: Spinner
    private lateinit var dateField: EditText
    private lateinit var timeField: EditText

    override lateinit var presenter: TrackHabitContract.Presenter

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
            timeField = findViewById(R.id.timeField)
        }

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_habit_done)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                presenter.submitTracking()
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