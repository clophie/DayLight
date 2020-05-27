package com.daylight.trackmood

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.daylight.R
import com.daylight.data.moods.Mood
import com.daylight.moods.MoodsActivity
import com.daylight.trackmood.TrackMoodContract
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


/**
 * Main UI for the track mood screen.
 */
class TrackMoodFragment : Fragment(), TrackMoodContract.View {

    private lateinit var moodSpinner: Spinner
    private lateinit var dateField: EditText
    private lateinit var dateButton: Button

    private lateinit var selectedMoodName: String

    override lateinit var presenter: TrackMoodContract.Presenter

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.trackmood_frag, container, false)
        setHasOptionsMenu(true)
        with(root) {
            moodSpinner = findViewById(R.id.mood_spinner)
            dateField = findViewById(R.id.dateField)
            dateButton = findViewById(R.id.dateButton)
        }

        // Set up date and time pop ups
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)

        dateField.setText("$day/$month/$year")

        dateButton.setOnClickListener {
            val dpd =
                DatePickerDialog(activity!!,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                        // Display Selected date in textbox
                        dateField.setText("$dayOfMonth/$monthOfYear/$year")

                        c.set(Calendar.YEAR, year)
                        c.set(Calendar.MONTH, monthOfYear)
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    }, year, month - 1, day)

            dpd.show()
        }

        // Set up floating action button
        activity!!.findViewById<FloatingActionButton>(R.id.fab_confirm_track_mood)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                c.set(Calendar.SECOND, 0)
                c.set(Calendar.MILLISECOND, 0)
                c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1)
                presenter.submitTracking(selectedMoodName, c)

                // Redirect back to the moods screen
                val intent = Intent(context, MoodsActivity::class.java)
                intent.putExtra("SNACKBAR_CONTENT", "Mood Tracked!")
                startActivity(intent)
            }
        }

        presenter.loadMoods()

        return root
    }

    override fun populateSpinner(moods: List<Mood>) {
        val moodTitles = moods.map { it.name }

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_item,
            moodTitles
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Add on selected listener to store the moodid of the currently selected mood
        moodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                pos: Int,
                id: Long
            ) {
                selectedMoodName = moods[pos].name
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Apply the adapter to the spinner
        moodSpinner.adapter = adapter
    }

    companion object {

        fun newInstance() =
            TrackMoodFragment().apply {
            }
    }
}