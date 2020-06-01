package com.daylight.habitdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.daylight.R
import com.daylight.addedithabit.AddEditHabitActivity
import com.daylight.addedithabit.AddEditHabitFragment
import com.daylight.data.habits.HabitTracking
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


/**
 * Main UI for the habit detail screen.
 */
class HabitDetailFragment : Fragment(), HabitDetailContract.View {

    private lateinit var detailTitle: TextView
    private lateinit var detailDescription: TextView
    private lateinit var habitTrackingList: ListView

    override lateinit var presenter: HabitDetailContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.habitdetail_frag, container, false)
        setHasOptionsMenu(true)
        with(root) {
            detailTitle = findViewById(R.id.habit_detail_title)
            detailDescription = findViewById(R.id.habit_detail_description)
            habitTrackingList = findViewById(R.id.habit_tracking_list)
        }

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_habit)?.setOnClickListener {
            presenter.editHabit()
        }

        habitTrackingList.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { adapter, v, position, p3 ->
                val trackingDate = adapter!!.getItemAtPosition(position)

                val cal = Calendar.getInstance()
                val sdf =
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
                cal.time = sdf.parse(trackingDate as String)
                cal.set(Calendar.AM_PM, 1)

                presenter.deleteHabitTracking(cal)

                fragmentManager?.beginTransaction()?.detach(this@HabitDetailFragment)?.attach(this@HabitDetailFragment)?.commit()

                presenter.loadHabitTracking()

                true
            }

        return root
    }

    override fun setLoadingIndicator(active: Boolean) {
        if (active) {
            detailTitle.text = ""
            detailDescription.text = getString(R.string.loading)
        }
    }

    override fun hideDescription() {
        detailDescription.visibility = View.GONE
    }

    override fun hideTitle() {
        detailTitle.visibility = View.GONE
    }

    override fun showDescription(description: String) {
        with(detailDescription) {
            visibility = View.VISIBLE
            text = description
        }
    }

    override fun showEditHabit(habitId: String) {
        val intent = Intent(context, AddEditHabitActivity::class.java)
        intent.putExtra(AddEditHabitFragment.ARGUMENT_EDIT_HABIT_ID, habitId)
        startActivityForResult(intent, REQUEST_EDIT_HABIT)
    }

    override fun showHabitDeleted() {
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_HABIT) {
            // If the habit was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                activity?.finish()
            }
        }
    }

    override fun showTitle(title: String) {
        with(detailTitle) {
            visibility = View.VISIBLE
            text = title
        }
    }

    override fun showMissingHabit() {
        detailTitle.text = ""
        detailDescription.text = getString(R.string.no_data)
    }

    override fun showHabitTracking(habitTracking: List<HabitTracking>) {

        var habitTrackingTimes = habitTracking.map { "${
        it.completionDateTime.get(Calendar.DAY_OF_MONTH)}/${
        it.completionDateTime.get(Calendar.MONTH) + 1}/${
        it.completionDateTime.get(Calendar.YEAR)} ${
        String.format("%02d:%02d", it.completionDateTime.get(Calendar.HOUR_OF_DAY), it.completionDateTime.get(Calendar.MINUTE))}" }

        // Display a message if the habit hasn't been tracked
        if (habitTracking.isEmpty()) {
            habitTrackingTimes = listOf("No tracking recorded for this habit!")
        }

        val itemsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1, habitTrackingTimes.toMutableList())

        habitTrackingList.adapter = itemsAdapter
    }

    companion object {

        private val ARGUMENT_HABIT_ID = "HABIT_ID"

        private val REQUEST_EDIT_HABIT = 1

        fun newInstance(habitId: String?) =
            HabitDetailFragment().apply {
                arguments = Bundle().apply { putString(ARGUMENT_HABIT_ID, habitId) }
            }
    }

}
