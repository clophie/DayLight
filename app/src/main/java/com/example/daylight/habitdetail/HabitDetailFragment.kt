package com.example.daylight.habitdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.daylight.R
import com.example.daylight.addedithabit.AddEditHabitActivity
import com.example.daylight.addedithabit.AddEditHabitFragment
import com.example.daylight.util.showSnackBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


/**
 * Main UI for the habit detail screen.
 */
class HabitDetailFragment : Fragment(), HabitDetailContract.View {

    private lateinit var detailTitle: TextView
    private lateinit var detailDescription: TextView
    private lateinit var detailCompleteStatus: CheckBox

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
            detailCompleteStatus = findViewById(R.id.habit_detail_complete)
        }

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_habit)?.setOnClickListener {
            presenter.editHabit()
        }

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val deletePressed = item.itemId == R.id.menu_delete
        if (deletePressed) presenter.deleteHabit()
        return deletePressed
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.habitdetail_fragment_menu, menu)
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

    override fun showCompletionStatus(complete: Boolean) {
        with(detailCompleteStatus) {
            isChecked = complete
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    presenter.completeHabit()
                } else {
                    presenter.activateHabit()
                }
            }
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

    override fun showHabitMarkedComplete() {
        view?.showSnackBar(getString(R.string.habit_marked_complete), Snackbar.LENGTH_LONG)
    }

    override fun showHabitMarkedActive() {
        view?.showSnackBar(getString(R.string.habit_marked_active), Snackbar.LENGTH_LONG)
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

    companion object {

        private val ARGUMENT_HABIT_ID = "HABIT_ID"

        private val REQUEST_EDIT_HABIT = 1

        fun newInstance(habitId: String?) =
            HabitDetailFragment().apply {
                arguments = Bundle().apply { putString(ARGUMENT_HABIT_ID, habitId) }
            }
    }

}
